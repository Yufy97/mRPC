package com.nineSeven.mrpc.core.codec;

import com.nineSeven.mrpc.core.protocol.MessageHeader;
import com.nineSeven.mrpc.core.protocol.MessageProtocol;
import com.nineSeven.mrpc.core.serialization.RpcSerialization;
import com.nineSeven.mrpc.core.serialization.SerializationFactory;
import com.nineSeven.mrpc.core.serialization.SerializationTypeEnum;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * 编码器
 * @param <T>
 */
public class RpcEncoder<T> extends MessageToByteEncoder<MessageProtocol<T>> {

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, MessageProtocol<T> messageProtocol, ByteBuf byteBuf) throws Exception {
        MessageHeader header = messageProtocol.getHeader();

        byteBuf.writeShort(header.getMagic())
                .writeByte(header.getVersion())
                .writeByte(header.getSerialization())
                .writeByte(header.getMsgType())
                .writeByte(header.getStatus())
                .writeCharSequence(header.getRequestId(), StandardCharsets.UTF_8);

        RpcSerialization rpcSerialization = SerializationFactory.getRpcSerialization(SerializationTypeEnum.parseByType(header.getMsgType()));
        byte[] data = rpcSerialization.serialize(messageProtocol.getBody());

        byteBuf.writeInt(data.length);
        byteBuf.writeBytes(data);
    }
}
