package com.telink.ble.mesh.ui;

import android.text.format.Time;

import com.telink.ble.mesh.core.message.MeshMessage;
import com.telink.ble.mesh.foundation.MeshService;
import com.telink.ble.mesh.model.NetworkingDevice;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class TransMeshMessage {
    private static TransMeshMessage mThis = new TransMeshMessage();

    public static TransMeshMessage getInstance() {
        return mThis;
    }

    public void DeviceBindSuccess(int address) {
        byte[] params = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN)
                .put((byte) 4)
                .put((byte) 1)
                .putShort((short) address)
                .array();
        MeshMessage meshMessage = createVendorMessage(address, MeshMessage.OPCODE_INVALID, params, -1);
        MeshService.getInstance().sendMeshMessage(meshMessage);
    }

    public void DeviceReSet(int address) {
        byte[] params = ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN)
                .put((byte) 2)
                .put((byte) 0x18)
                .array();
        MeshMessage meshMessage = createVendorMessage(address, MeshMessage.OPCODE_INVALID, params, -1);
        MeshService.getInstance().sendMeshMessage(meshMessage);
    }

    public void SendDeviceByte(int address, byte n) {
        byte[] params = ByteBuffer.allocate(1).order(ByteOrder.LITTLE_ENDIAN)
                .put(n)
                .array();
        MeshMessage meshMessage = createVendorMessage(address, MeshMessage.OPCODE_INVALID, params, -1);
        MeshService.getInstance().sendMeshMessage(meshMessage);
    }

    public void SyncDeviceSystemTime(int address) {
        Time t = new Time();
        t.setToNow();
        int hour = t.hour;
        int min = t.minute;
        int sec = t.second;

        byte[] params = ByteBuffer.allocate(5).order(ByteOrder.LITTLE_ENDIAN)
                .put((byte) 5)
                .put((byte) 2)
                .put((byte) hour)
                .put((byte) min)
                .put((byte) sec)
                .array();
        MeshMessage meshMessage = createVendorMessage(address, MeshMessage.OPCODE_INVALID, params, -1);
        MeshService.getInstance().sendMeshMessage(meshMessage);
    }

    public void SetTimer(int address, int id, int hour, int min) {
        byte[] params = ByteBuffer.allocate(6).order(ByteOrder.LITTLE_ENDIAN)
                .put((byte) 6)
                .put((byte) 3)
                .put((byte) id)
                .put((byte) hour)
                .put((byte) min)
                .put((byte) 0)
                .array();
        MeshMessage meshMessage = createVendorMessage(address, MeshMessage.OPCODE_INVALID, params, -1);
        MeshService.getInstance().sendMeshMessage(meshMessage);
    }

    public void DelTimer(int address, int id) {
        byte[] params = ByteBuffer.allocate(3).order(ByteOrder.LITTLE_ENDIAN)
                .put((byte) 3)
                .put((byte) 4)
                .put((byte) id)
                .array();
        MeshMessage meshMessage = createVendorMessage(address, MeshMessage.OPCODE_INVALID, params, -1);
        MeshService.getInstance().sendMeshMessage(meshMessage);
    }

    public void SetDeviceTimer(int address, int id, int hour, int min) {
        byte[] params = ByteBuffer.allocate(6).order(ByteOrder.LITTLE_ENDIAN)
                .put((byte) 6)
                .put((byte) 0x16)
                .put((byte) id)
                .put((byte) hour)
                .put((byte) min)
                .put((byte) 0)
                .array();
        MeshMessage meshMessage = createVendorMessage(address, MeshMessage.OPCODE_INVALID, params, -1);
        MeshService.getInstance().sendMeshMessage(meshMessage);
    }

    public void DelDeviceTimer(int address, int id) {
        byte[] params = ByteBuffer.allocate(3).order(ByteOrder.LITTLE_ENDIAN)
                .put((byte) 3)
                .put((byte) 0x17)
                .put((byte) id)
                .array();
        MeshMessage meshMessage = createVendorMessage(address, MeshMessage.OPCODE_INVALID, params, -1);
        MeshService.getInstance().sendMeshMessage(meshMessage);
    }

    public void SetDeviceDelay(int address, int delay) {
        byte[] params = ByteBuffer.allocate(3).order(ByteOrder.LITTLE_ENDIAN)
                .put((byte) 3)
                .put((byte) 5)
                .put((byte) delay)
                .array();
        MeshMessage meshMessage = createVendorMessage(address, MeshMessage.OPCODE_INVALID, params, -1);
        MeshService.getInstance().sendMeshMessage(meshMessage);
    }

    public void SetOnOff(int address, int onOff) {
        byte[] params = ByteBuffer.allocate(3).order(ByteOrder.LITTLE_ENDIAN)
                .put((byte) 3)
                .put((byte) 6)
                .put((byte) onOff)
                .array();
        MeshMessage meshMessage = createVendorMessage(address, MeshMessage.OPCODE_INVALID, params, -1);
        MeshService.getInstance().sendMeshMessage(meshMessage);
    }

    public void SetBrodCastOnOff(int groupAddress, int onOff) {
        byte[] params = ByteBuffer.allocate(5).order(ByteOrder.LITTLE_ENDIAN)
                .put((byte) 5)
                .put((byte) 0x11)
                .putShort((short)groupAddress)
                .put((byte) onOff)
                .array();
        MeshMessage meshMessage = createVendorMessage(0xffff, MeshMessage.OPCODE_INVALID, params, -1);
        MeshService.getInstance().sendMeshMessage(meshMessage);
    }

    public void SetGroup(int address, int groupAddress, int opType) {
        int opCode = (opType == 1) ? 0xF : 0x10;
        byte[] params = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN)
                .put((byte) 4)
                .put((byte) opCode)
                .putShort((short) groupAddress)
                .array();
        MeshMessage meshMessage = createVendorMessage(address, MeshMessage.OPCODE_INVALID, params, -1);
        MeshService.getInstance().sendMeshMessage(meshMessage);
    }

    public void SetGroupLum(int groupAddress, int progress) {
        byte[] params = ByteBuffer.allocate(5).order(ByteOrder.LITTLE_ENDIAN)
                .put((byte) 5)
                .put((byte) 0x12)
                .putShort((short)groupAddress)
                .put((byte) progress)
                .array();
        MeshMessage meshMessage = createVendorMessage(0xffff, MeshMessage.OPCODE_INVALID, params, -1);
        MeshService.getInstance().sendMeshMessage(meshMessage);
    }

    public void SetGroupTemp(int groupAddress, int progress) {
        byte[] params = ByteBuffer.allocate(5).order(ByteOrder.LITTLE_ENDIAN)
                .put((byte) 5)
                .put((byte) 0x13)
                .putShort((short)groupAddress)
                .put((byte) progress)
                .array();
        MeshMessage meshMessage = createVendorMessage(0xffff, MeshMessage.OPCODE_INVALID, params, -1);
        MeshService.getInstance().sendMeshMessage(meshMessage);
    }

    public void SetDeviceLum(int address, int progress) {
        byte[] params = ByteBuffer.allocate(3).order(ByteOrder.LITTLE_ENDIAN)
                .put((byte) 3)
                .put((byte) 0x0E)
                .put((byte) progress)
                .array();
        MeshMessage meshMessage = createVendorMessage(address, MeshMessage.OPCODE_INVALID, params, -1);
        MeshService.getInstance().sendMeshMessage(meshMessage);
    }
    public void SetAdjoin(int address, int adjoinAddress, int opType) {
        int opCode = (opType == 1) ? 0x14 : 0x15;
        byte[] params = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN)
                .put((byte) 4)
                .put((byte) opCode)
                .putShort((short) adjoinAddress)
                .array();
        MeshMessage meshMessage = createVendorMessage(address, MeshMessage.OPCODE_INVALID, params, -1);
        MeshService.getInstance().sendMeshMessage(meshMessage);
    }

    private MeshMessage createVendorMessage(int destinationAddress, int rspOpcode, byte[] params, int tidPosition) {
        MeshMessage meshMessage = new MeshMessage();
        meshMessage.setDestinationAddress(destinationAddress);
        meshMessage.setOpcode(0x0211C5);
        meshMessage.setParams(params);
        meshMessage.setResponseOpcode(rspOpcode);
//        meshMessage.setResponseMax(0);
//        meshMessage.setRetryCnt(2);
//        meshMessage.setTtl(5);
        meshMessage.setTidPosition(tidPosition);
        return meshMessage;
    }
}
