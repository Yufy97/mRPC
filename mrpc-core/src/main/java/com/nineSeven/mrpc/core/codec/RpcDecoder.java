package com.nineSeven.mrpc.core.codec;

import com.nineSeven.mrpc.core.common.RpcRequest;
import com.nineSeven.mrpc.core.common.RpcResponse;
import com.nineSeven.mrpc.core.protocol.MessageHeader;
import com.nineSeven.mrpc.core.protocol.MessageProtocol;
import com.nineSeven.mrpc.core.protocol.MsgType;
import com.nineSeven.mrpc.core.protocol.ProtocolConstants;
import com.nineSeven.mrpc.core.serialization.RpcSerialization;
import com.nineSeven.mrpc.core.serialization.SerializationFactory;
import com.nineSeven.mrpc.core.serialization.SerializationTypeEnum;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static com.nineSeven.mrpc.core.protocol.MsgType.REQUEST;

/**
 * 解码器
 */
public class RpcDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        if (byteBuf.readableBytes() < ProtocolConstants.HEADER_TOTAL_LEN) {
            return;
        }

        byteBuf.markReaderIndex();
        short magic = byteBuf.readShort();
        if (magic != ProtocolConstants.MAGIC) {
            throw new IllegalArgumentException("magic number is illegal!");
        }

        byte version = byteBuf.readByte();
        byte serialization = byteBuf.readByte();
        byte msgType = byteBuf.readByte();
        MsgType msgTypeEnum = MsgType.findByType(msgType);
        if (msgTypeEnum == null) {
            return;
        }
        byte status = byteBuf.readByte();
        String requestId = byteBuf.readCharSequence(ProtocolConstants.REQ_LEN, StandardCharsets.UTF_8).toString();
        int msgLen = byteBuf.readInt();
        if (byteBuf.readableBytes() < msgLen) {
            byteBuf.resetReaderIndex();
            return;
        }
        byte[] data = new byte[msgLen];
        byteBuf.readBytes(data);

        MessageHeader messageHeader = new MessageHeader();
        messageHeader.setMagic(magic);
        messageHeader.setVersion(version);
        messageHeader.setSerialization(serialization);
        messageHeader.setMsgType(msgType);
        messageHeader.setStatus(status);
        messageHeader.setRequestId(requestId);
        messageHeader.setMsgLen(msgLen);

        RpcSerialization rpcSerialization = SerializationFactory.getRpcSerialization(SerializationTypeEnum.parseByType(serialization));
        switch (msgTypeEnum) {
            case REQUEST:
                RpcRequest rpcRequest = rpcSerialization.deserialize(data, RpcRequest.class);
                if (rpcRequest != null) {
                    MessageProtocol<RpcRequest> protocol = new MessageProtocol<>();
                    protocol.setHeader(messageHeader);
                    protocol.setBody(rpcRequest);
                    list.add(protocol);
                }
                break;
            case RESPONSE:
                RpcResponse rpcResponse = rpcSerialization.deserialize(data, RpcResponse.class);
                if (rpcResponse != null) {
                    MessageProtocol<RpcResponse> protocol = new MessageProtocol<>();
                    protocol.setHeader(messageHeader);
                    protocol.setBody(rpcResponse);
                    list.add(protocol);
                }
                break;
            default:
                throw new IllegalArgumentException("msgTypeEnum is illegal!");
        }
    }
}
