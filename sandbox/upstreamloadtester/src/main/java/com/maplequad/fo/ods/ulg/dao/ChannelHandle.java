package com.maplequad.fo.ods.ulg.dao;

import foods.bigtable.repository.Database;
import org.apache.hadoop.hbase.client.Connection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.grpc.Channel;
import io.grpc.ManagedChannelBuilder;

public class ChannelHandle {
    private static final Logger LOGGER = LoggerFactory.getLogger(ChannelHandle.class);
    private Channel channel = null;
    private static ChannelHandle instance;

    /**
     * This constructor is used  to create an instance of ChannelHandle for a given google repoHostName and port
     *
     * @param repoHostName
     * @param port
     * @throws Exception
     */
    public static ChannelHandle getInstance(String repoHostName, int port) {
        if (instance == null) {
            synchronized (ChannelHandle.class) {
                if (instance == null) {
                    instance = new ChannelHandle();
                    instance.setChannel(ManagedChannelBuilder.forAddress(repoHostName, port).usePlaintext(true).build());
                }
            }
        }
        return instance;
    }

    /***
     * Default constructor is made private
     */
    private ChannelHandle() {
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }
}
