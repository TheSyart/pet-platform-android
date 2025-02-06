package com.example.petstore.pojo;

public class Message {
    private String receiver;

    private Integer receiverType;

    private String sender;

    private Integer sendType;

    private String messageCover;

    private String messageLength;

    private String message;

    private Integer messageType;

    public Message(String receiver, Integer receiverType, String sender, Integer sendType, String messageCover, String messageLength, String message, Integer messageType) {
        this.receiver = receiver;
        this.receiverType = receiverType;
        this.sender = sender;
        this.sendType = sendType;
        this.messageCover = messageCover;
        this.messageLength = messageLength;
        this.message = message;
        this.messageType = messageType;
    }

    public String getMessageCover() {
        return messageCover;
    }

    public void setMessageCover(String messageCover) {
        this.messageCover = messageCover;
    }

    public String getMessageLength() {
        return messageLength;
    }

    public void setMessageLength(String messageLength) {
        this.messageLength = messageLength;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public Integer getReceiverType() {
        return receiverType;
    }

    public void setReceiverType(Integer receiverType) {
        this.receiverType = receiverType;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public Integer getSendType() {
        return sendType;
    }

    public void setSendType(Integer sendType) {
        this.sendType = sendType;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getMessageType() {
        return messageType;
    }

    public void setMessageType(Integer messageType) {
        this.messageType = messageType;
    }

    @Override
    public String toString() {
        return "Message{" +
                "receiver='" + receiver + '\'' +
                ", receiverType=" + receiverType +
                ", sender='" + sender + '\'' +
                ", sendType=" + sendType +
                ", message='" + messageCover + '\'' +
                ", message='" + messageLength + '\'' +
                ", message='" + message + '\'' +
                ", messageType=" + messageType +
                '}';
    }

}
