package com.letv.portal.service.openstack.resource.service;

import com.letv.portal.service.openstack.exception.OpenStackException;
import com.letv.portal.service.openstack.resource.VMResource;
import org.jclouds.openstack.neutron.v2.NeutronApi;
import org.jclouds.openstack.nova.v2_0.NovaApi;

import java.util.List;

/**
 * Created by zhouxianguang on 2015/10/30.
 */
public interface ResourceService {

    void attachVmToSubnet(NovaApi novaApi, NeutronApi neutronApi, String region, String vmId, String subnetId) throws OpenStackException;

    void detachVmFromSubnet(NovaApi novaApi, NeutronApi neutronApi, String region, String vmId, String subnetId) throws OpenStackException;

    List<VMResource> listVmNotInAnyNetwork(NovaApi novaApi, NeutronApi neutronApi, String region) throws OpenStackException;

    List<VMResource> listVmAttachedSubnet(NovaApi novaApi, NeutronApi neutronApi, String region, String subnetId) throws OpenStackException;

}
