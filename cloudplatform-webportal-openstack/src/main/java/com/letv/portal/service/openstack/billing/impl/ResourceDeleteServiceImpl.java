package com.letv.portal.service.openstack.billing.impl;

import com.google.common.collect.ImmutableSet;
import com.letv.common.exception.MatrixException;
import com.letv.portal.service.openstack.billing.BillingResource;
import com.letv.portal.service.openstack.billing.ResourceDeleteService;
import com.letv.portal.service.openstack.billing.ResourceLocator;
import com.letv.portal.service.openstack.cronjobs.VmSyncService;
import com.letv.portal.service.openstack.exception.OpenStackException;
import com.letv.portal.service.openstack.impl.OpenStackServiceImpl;
import com.letv.portal.service.openstack.jclouds.service.ApiService;
import com.letv.portal.service.openstack.local.service.LocalVolumeService;
import com.letv.portal.service.openstack.resource.FloatingIpResource;
import com.letv.portal.service.openstack.resource.RouterResource;
import com.letv.portal.service.openstack.resource.VMResource;
import com.letv.portal.service.openstack.resource.VolumeResource;
import com.letv.portal.service.openstack.resource.service.ResourceService;
import com.letv.portal.service.openstack.util.ExceptionUtil;
import com.letv.portal.service.openstack.util.RandomUtil;
import com.letv.portal.service.openstack.util.ThreadUtil;
import com.letv.portal.service.openstack.util.Timeout;
import com.letv.portal.service.openstack.util.function.Function;
import com.letv.portal.service.openstack.util.function.Function1;
import org.jclouds.openstack.cinder.v1.CinderApi;
import org.jclouds.openstack.cinder.v1.domain.Snapshot;
import org.jclouds.openstack.cinder.v1.domain.Volume;
import org.jclouds.openstack.cinder.v1.domain.VolumeAttachment;
import org.jclouds.openstack.cinder.v1.features.SnapshotApi;
import org.jclouds.openstack.cinder.v1.features.VolumeApi;
import org.jclouds.openstack.neutron.v2.NeutronApi;
import org.jclouds.openstack.neutron.v2.domain.FloatingIP;
import org.jclouds.openstack.neutron.v2.domain.IP;
import org.jclouds.openstack.neutron.v2.domain.Port;
import org.jclouds.openstack.neutron.v2.domain.Router;
import org.jclouds.openstack.neutron.v2.extensions.FloatingIPApi;
import org.jclouds.openstack.neutron.v2.extensions.RouterApi;
import org.jclouds.openstack.neutron.v2.features.PortApi;
import org.jclouds.openstack.nova.v2_0.NovaApi;
import org.jclouds.openstack.nova.v2_0.domain.Server;
import org.jclouds.openstack.nova.v2_0.extensions.VolumeAttachmentApi;
import org.jclouds.openstack.nova.v2_0.features.ServerApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by zhouxianguang on 2015/10/28.
 */
@Service
public class ResourceDeleteServiceImpl implements ResourceDeleteService {

    @Autowired
    private ApiService apiService;

    @Autowired
    private ResourceService resourceService;

    @Autowired
    private VmSyncService vmSyncService;

    @Autowired
    private LocalVolumeService localVolumeService;

    @Override
    public void deleteResource(final long userId, Iterable<ResourceLocator> resourceLocators) throws MatrixException {
        try {
            final String randomSessionId = RandomUtil.generateRandomSessionId();
            apiService.loadAllApiForRandomSessionFromBackend(userId, randomSessionId);
            try {
                Map<Class<? extends BillingResource>, List<ResourceLocator>> typeToLocators = new HashMap<Class<? extends BillingResource>, List<ResourceLocator>>();

                for (Iterator<ResourceLocator> it = resourceLocators.iterator(); it.hasNext(); ) {
                    ResourceLocator resourceLocator = it.next();
                    List<ResourceLocator> locatorList = typeToLocators.get(resourceLocator.type());
                    if (locatorList == null) {
                        locatorList = new LinkedList<ResourceLocator>();
                        typeToLocators.put(resourceLocator.type(), locatorList);
                    }
                    locatorList.add(resourceLocator);
                }

                for (Map.Entry<Class<? extends BillingResource>, List<ResourceLocator>> typeToLocatorsEntry : typeToLocators.entrySet()) {
                    ThreadUtil.concurrentFilter(typeToLocatorsEntry.getValue(), new Function1<Void, ResourceLocator>() {
                        @Override
                        public Void apply(ResourceLocator resourceLocator) throws Exception {
                            deleteBillingResource(userId, randomSessionId, resourceLocator);
                            return null;
                        }
                    });
                }
            } finally {
                apiService.clearCache(userId, randomSessionId);
            }
        } catch (Exception ex) {
            ExceptionUtil.throwMatrixException(ex);
        }
    }

    private void deleteBillingResource(long userId, String randomSessionId, ResourceLocator resourceLocator) throws OpenStackException {
        if (resourceLocator.getType() == VMResource.class) {
            deleteServer(userId, randomSessionId, resourceLocator);
        } else if (resourceLocator.getType() == RouterResource.class) {
            deleteRouter(userId, randomSessionId, resourceLocator);
        } else if (resourceLocator.getType() == FloatingIpResource.class) {
            deleteFloatingIp(userId, randomSessionId, resourceLocator);
        } else if (resourceLocator.getType() == VolumeResource.class) {
            deleteVolume(userId, randomSessionId, resourceLocator);
        }
    }

    private void deleteVolume(long userId, String sessionId, final ResourceLocator locator) throws OpenStackException {
        CinderApi cinderApi = apiService.getCinderApi(userId, sessionId);
        resourceService.checkRegion(locator.region(), cinderApi);
        final VolumeApi volumeApi = cinderApi.getVolumeApi(locator.region());
        Volume volume = volumeApi.get(locator.id());
        if (volume != null) {
            if (!volume.getAttachments().isEmpty()) {
                NovaApi novaApi = apiService.getNovaApi(userId, sessionId);
                final VolumeAttachmentApi volumeAttachmentApi = novaApi.getVolumeAttachmentApi(locator.region()).get();
                for (final VolumeAttachment volumeAttachment : volume.getAttachments()) {
                    boolean isSuccess = volumeAttachmentApi.detachVolumeFromServer(volumeAttachment.getVolumeId(), volumeAttachment.getServerId());
                    if (isSuccess) {
                        localVolumeService.update(userId, userId, locator.region(), volume);
                        ThreadUtil.waiting(new Function<Boolean>() {
                            @Override
                            public Boolean apply() throws Exception {
                                return volumeAttachmentApi.getAttachmentForVolumeOnServer(volumeAttachment.getVolumeId(), volumeAttachment.getServerId()) != null;
                            }
                        }, new Timeout().time(10L).unit(TimeUnit.MINUTES));
                        localVolumeService.update(userId, userId, locator.region(), volume);
                    }
                }
            }

            final SnapshotApi snapshotApi = cinderApi.getSnapshotApi(locator.region());
            for (final Snapshot snapshot : snapshotApi.list().toList()) {
                if (volume.getId().equals(snapshot.getVolumeId())) {
                    boolean isSuccess = snapshotApi.delete(snapshot.getId());
                    if (isSuccess) {
                        ThreadUtil.waiting(new Function<Boolean>() {
                            @Override
                            public Boolean apply() throws Exception {
                                return snapshotApi.get(snapshot.getId()) != null;
                            }
                        }, new Timeout().time(10L).unit(TimeUnit.MINUTES));
                    }
                }
            }

            ThreadUtil.waiting(new Function<Boolean>() {
                @Override
                public Boolean apply() throws Exception {
                    Volume latestVolume = volumeApi.get(locator.id());
                    if (latestVolume == null) {
                        return false;
                    }
                    Volume.Status status = latestVolume.getStatus();
                    return status == Volume.Status.CREATING || status == Volume.Status.ATTACHING || status == Volume.Status.DELETING || status == Volume.Status.ERROR_DELETING;
                }
            }, new Timeout().time(10L).unit(TimeUnit.MINUTES));
            if (volumeApi.get(locator.id()) == null) {
                return;
            }

            boolean isSuccess = volumeApi.delete(locator.id());
            if (isSuccess) {
                ThreadUtil.waiting(new Function<Boolean>() {
                    @Override
                    public Boolean apply() throws Exception {
                        return volumeApi.get(locator.id()) != null;
                    }
                }, new Timeout().time(10L).unit(TimeUnit.MINUTES));
                OpenStackServiceImpl.getOpenStackServiceGroup().getLocalVolumeService().delete(userId, locator.region(), volume.getId());
            }
        }
    }

    private void deleteFloatingIp(long userId, String sessionId, final ResourceLocator locator) throws OpenStackException {
        NeutronApi neutronApi = apiService.getNeutronApi(userId, sessionId);
        resourceService.checkRegion(locator.region(), neutronApi);
        final FloatingIPApi floatingIPApi = neutronApi.getFloatingIPApi(locator.region()).get();
        final FloatingIP floatingIP = floatingIPApi.get(locator.id());
        if (floatingIP != null) {
            boolean isSuccess = floatingIPApi.delete(locator.id());
            if (isSuccess) {
                ThreadUtil.waiting(new Function<Boolean>() {
                    @Override
                    public Boolean apply() throws Exception {
                        return floatingIPApi.get(locator.id()) != null;
                    }
                }, new Timeout().time(10L).unit(TimeUnit.MINUTES));
            }
        }
    }

    private void deleteRouter(long userId, String sessionId, final ResourceLocator locator) throws OpenStackException {
        NeutronApi neutronApi = apiService.getNeutronApi(userId, sessionId);
        resourceService.checkRegion(locator.region(), neutronApi);
        final RouterApi routerApi = neutronApi.getRouterApi(locator.region()).get();
        Router router = routerApi.get(locator.id());
        if (router != null) {
            final PortApi portApi = neutronApi.getPortApi(locator.region());
            for (final Port port : portApi.list().concat()
                    .toList()) {
                if ("network:router_interface"
                        .equals(port.getDeviceOwner())
                        && router.getId().equals(port.getDeviceId())) {
                    ImmutableSet<IP> fixedIps = port.getFixedIps();
                    if (fixedIps != null) {
                        for (IP ip : fixedIps) {
                            if (ip.getSubnetId() != null) {
                                boolean isSuccess = routerApi.removeInterfaceForSubnet(router.getId(), ip.getSubnetId());
                                if (isSuccess) {
                                    ThreadUtil.waiting(new Function<Boolean>() {
                                        @Override
                                        public Boolean apply() throws Exception {
                                            return portApi.get(port.getId()) != null;
                                        }
                                    }, new Timeout().time(10L).unit(TimeUnit.MINUTES));
                                }
                            }
                        }
                    }
                }
            }

            boolean isSuccess = routerApi.delete(locator.id());
            if (isSuccess) {
                ThreadUtil.waiting(new Function<Boolean>() {
                    @Override
                    public Boolean apply() throws Exception {
                        return routerApi.get(locator.id()) != null;
                    }
                }, new Timeout().time(10L).unit(TimeUnit.MINUTES));
            }
        }
    }

    private void deleteServer(long userId, String sessionId, final ResourceLocator locator) throws OpenStackException {
        NovaApi novaApi = apiService.getNovaApi(userId, sessionId);
        resourceService.checkRegion(locator.region(), novaApi);
        final ServerApi serverApi = novaApi.getServerApi(locator.region());
        Server server = serverApi.get(locator.id());
        if (server != null) {

            boolean isSuccess = serverApi.delete(locator.id());
            if (isSuccess) {
                ThreadUtil.waiting(new Function<Boolean>() {
                    @Override
                    public Boolean apply() throws Exception {
                        return serverApi.get(locator.id()) != null;
                    }
                }, new Timeout().time(60L).unit(TimeUnit.MINUTES));
                vmSyncService.recordVmDeleted(userId, locator.region(), server.getId());
            }
        }
    }

}
