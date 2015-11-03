package com.letv.portal.service.openstack.util;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.letv.common.exception.MatrixException;
import com.letv.portal.service.openstack.exception.OpenStackException;
import com.letv.portal.service.openstack.util.function.Function;
import com.letv.portal.service.openstack.util.function.Function1;
import org.apache.commons.collections.ListUtils;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

/**
 * Created by zhouxianguang on 2015/10/30.
 */
public class ThreadUtil {
    private static final ListeningExecutorService executorService = MoreExecutors.listeningDecorator(Executors.newCachedThreadPool());

    public static void asyncExec(Runnable task) {
        executorService.submit(task);
    }

    public static void concurrentRunAndWait(Runnable currentThreadTask, Runnable... otherTasks) {
        ListenableFuture[] futures = new ListenableFuture[otherTasks.length];
        for (int i = 0; i < otherTasks.length; i++) {
            futures[i] = executorService.submit(otherTasks[i]);
        }

        currentThreadTask.run();

        try {
            Futures.successfulAsList(futures).get();
        } catch (Exception e) {
            throw new MatrixException("后台错误", e);
        }
    }

    public static <T> List<Ref<T>> concurrentRunAndWait(Function<T> currentThreadTask, Function<T>... otherTasks) throws OpenStackException {
        return concurrentRunAndWait(null, currentThreadTask, otherTasks);
    }

    public static <T> List<Ref<T>> concurrentRunAndWait(Timeout timeout, Function<T> currentThreadTask, Function<T>... otherTasks) throws OpenStackException {
        try {
            ListenableFuture[] futures = new ListenableFuture[otherTasks.length];
            for (int i = 0; i < otherTasks.length; i++) {
                final Function<T> task = otherTasks[i];
                futures[i] = executorService.submit(new Callable<Ref<T>>() {
                    @Override
                    public Ref<T> call() throws Exception {
                        return new Ref<T>(task.apply());
                    }
                });
            }

            T firstResult = currentThreadTask.apply();

            List<Ref<T>> otherResultRefs;
            ListenableFuture<List<Ref<T>>> listFuture = Futures.successfulAsList(futures);
            if (timeout != null) {
                otherResultRefs = listFuture.get(timeout.time(), timeout.unit());
            } else {
                otherResultRefs = listFuture.get();
            }

            List<Ref<T>> resultList = new LinkedList<Ref<T>>();
            resultList.add(new Ref<T>(firstResult));
            if (otherResultRefs != null) {
                for (Ref<T> resultRef : otherResultRefs) {
                    resultList.add(resultRef);
                }
            } else {
                for (int i = 0; i < otherTasks.length; i++) {
                    resultList.add(new Ref<T>());
                }
            }
            return resultList;
        } catch (ExecutionException ex){
            ExceptionUtil.throwException(ExceptionUtil.getCause(ex));
        } catch (Exception ex) {
            ExceptionUtil.throwException(ex);
        }
        List<Ref<T>> resultList = new LinkedList<Ref<T>>();
        for (int i = 0; i <= otherTasks.length; i++) {
            resultList.add(new Ref<T>());
        }
        return resultList;
    }

    public static void concurrentRun(Runnable... tasks) {
        for (Runnable task : tasks) {
            executorService.submit(task);
        }
    }

    public static <PT, RT> List<RT> concurrentFilter(List<PT> list, final Function1<RT, PT> filter) throws OpenStackException {
        return concurrentFilter(list, filter, null);
    }

    public static <PT, RT> List<RT> concurrentFilter(List<PT> list, final Function1<RT, PT> filter, Timeout timeout) throws OpenStackException {
        try {
            if (list.isEmpty()) {
                return new LinkedList<RT>();
            }
            if (list.size() == 1) {
                List<RT> newList = new LinkedList<RT>();
                RT element = filter.apply(list.get(0));
                if (element != null) {
                    newList.add(element);
                }
                return newList;
            }

            List<ListenableFuture<Ref<RT>>> futures = new LinkedList<ListenableFuture<Ref<RT>>>();
            for (int i = 1; i < list.size(); i++) {
                final PT oldElement = list.get(i);
                futures.add(executorService.submit(new Callable<Ref<RT>>() {
                    @Override
                    public Ref<RT> call() throws Exception {
                        return new Ref<RT>(filter.apply(oldElement));
                    }
                }));
            }

            RT firstNewElement = filter.apply(list.get(0));

            ListenableFuture<List<Ref<RT>>> listFuture = Futures.successfulAsList(futures);
            List<Ref<RT>> otherNewElementRefs;
            if (timeout != null) {
                otherNewElementRefs = listFuture.get(timeout.time(), timeout.unit());
            } else {
                otherNewElementRefs = listFuture.get();
            }

            List<RT> newList = new LinkedList<RT>();
            if (firstNewElement != null) {
                newList.add(firstNewElement);
            }
            if (otherNewElementRefs != null) {
                for (Ref<RT> newElementRef : otherNewElementRefs) {
                    RT newElement = newElementRef.get();
                    if (newElement != null) {
                        newList.add(newElement);
                    }
                }
            }
            return newList;
        } catch (ExecutionException ex){
            ExceptionUtil.throwException(ExceptionUtil.getCause(ex));
        } catch (Exception ex) {
            ExceptionUtil.throwException(ex);
        }
        return new LinkedList<RT>();
    }

}