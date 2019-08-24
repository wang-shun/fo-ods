package com.maplequad.fo.ods.tradecore.lcm.utils;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.UUID;

public class CreateUUIDHelper {
	public static  long createUUID(){
		
		final UUID uid = UUID.randomUUID();
        final ByteBuffer buffer = ByteBuffer.wrap(new byte[16]);
        buffer.putLong(uid.getLeastSignificantBits());
        buffer.putLong(uid.getMostSignificantBits());
        final BigInteger bi = new BigInteger(buffer.array());
        return  bi.longValue() & Long.MAX_VALUE;
	}
}
