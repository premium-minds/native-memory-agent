package com.premiumminds.jvm.agent.nativememory;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;

public class StatsDSender implements AutoCloseable {

	private final DatagramSocket socket;
	private final InetSocketAddress address;

	public StatsDSender(String host, int port) throws SocketException {
		socket = new DatagramSocket();
		address = new InetSocketAddress(host, port);
	}

	public void sendEcho(String msg) throws IOException {
		byte[] buf = msg.getBytes();
		DatagramPacket packet
			= new DatagramPacket(buf, buf.length, address);
		socket.send(packet);
	}

	@Override
	public void close() {
		socket.close();
	}
}
