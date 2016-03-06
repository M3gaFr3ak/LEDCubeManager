package com.techjar.ledcm.hardware.handler;

import org.teleal.cling.binding.xml.Descriptor;
import org.usb4java.*;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

public class LibUsbHandler implements PortHandler {
    private Context context = new Context();
    private DeviceHandle bridgeHandle = new DeviceHandle();
    private boolean open;
    private IntBuffer allocate = IntBuffer.allocate(1);

    public LibUsbHandler() {
        int result = LibUsb.init(context);
        if (result < 0) {
            throw new LibUsbException("Unable to initialize libusb", result);
        }
    }

    @Override
    public boolean isOpened() {
        return open;
    }

    @Override
    public void open(int baudRate) throws IOException {
        DeviceList list = new DeviceList();
        if (LibUsb.getDeviceList(context, list) < 0)
            throw new LibUsbException("Unable to get device list", -1);
        Device bridge = null;

        try {
            for (Device device : list) {
                DeviceDescriptor descriptor = new DeviceDescriptor();
                if (LibUsb.getDeviceDescriptor(device, descriptor) < 0)
                    throw new LibUsbException("Unable to read device descriptor", -1);
                if (descriptor.idVendor() == (short) 0x1111 && descriptor.idProduct() == (short) 0x2222) {
                    bridge = device;
                    break;
                }
            }
            if (bridge == null)
                throw new LibUsbException("Unable to find PSoC", -1);
        } finally {
            LibUsb.freeDeviceList(list, false);
        }
        bridgeHandle = new DeviceHandle();
        int result = LibUsb.open(bridge, bridgeHandle);
        if (result < 0)
            throw new LibUsbException("Could not open device", result);
        else
            open = true;

        if (LibUsb.kernelDriverActive(bridgeHandle, 0) < 0)
            LibUsb.detachKernelDriver(bridgeHandle, 0);
        if (LibUsb.claimInterface(bridgeHandle, 0) < 0)
            throw new LibUsbException("Could not claim interface", result);
    }

    @Override
    public void close() throws IOException {
        if (bridgeHandle != null)
            LibUsb.close(bridgeHandle);
        open = false;
    }

    @Override
    public byte[] readBytes() throws IOException {
        return new byte[0];
    }

    @Override
    public byte[] readBytes(int count) throws IOException {
        return new byte[0];
    }

    @Override
    public byte[] readBytes(int count, int timeout) throws IOException {
        return new byte[0];
    }

    @Override
    public void writeByte(byte b) throws IOException {
        writeBytes(new byte[]{b});
    }

    @Override
    public void writeBytes(byte[] b) throws IOException {
        if (isOpened()) {
            ByteBuffer byteBuffer = ByteBuffer.allocateDirect(b.length);
            byteBuffer.put(b);
            LibUsb.bulkTransfer(bridgeHandle, (byte) 0x02, byteBuffer, allocate, 1000);
        }
    }
}
