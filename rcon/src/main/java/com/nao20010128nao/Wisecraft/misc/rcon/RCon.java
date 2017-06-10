package com.nao20010128nao.Wisecraft.misc.rcon;

import com.nao20010128nao.Wisecraft.misc.CompatUtils;
import com.nao20010128nao.Wisecraft.misc.compat.*;
import java.io.*;
import java.net.*;
import java.nio.*;
import java.security.*;
import java.util.*;

public class RCon {

	private static final int COMMAND_TYPE = 2;
	private static final int LOGIN_TYPE = 3;
	private final int requestId;
	private final Socket socket;
	private final InputStream inputStream;
	private final OutputStream outputStream;
	private final Object syncObject = new Object();

	public RCon(final String host, final int port, final char[] password) throws IOException, AuthenticationException {
		final SecureRandom random = new SecureRandom();
		requestId = random.nextInt();
		socket = new Socket(host, port);
		outputStream = socket.getOutputStream();
		inputStream = socket.getInputStream();
		final byte[] passwordBytes = new String(password).getBytes(CompatCharsets.UTF_8);
		final byte[] response = send(LOGIN_TYPE, passwordBytes);
		Arrays.fill(passwordBytes,(byte)0);
		assert response.length == 0;
	}

	public void ban(final String player) throws IOException, AuthenticationException {
		final StringBuilder sb = new StringBuilder();
		sb.append("ban");
		sb.append(' ').append(player);
		final String response = send(sb.toString());
		assert "".equals(response);
	}

	public void banIp(final String host) throws AuthenticationException, IOException {
		final StringBuilder sb = new StringBuilder();
		sb.append("ban-ip");
		sb.append(' ').append(host);
		final String response = send(sb.toString());
		assert "".equals(response);
	}

	public String[] banIPList() throws IOException, AuthenticationException {
		final StringBuilder sb = new StringBuilder();
		sb.append("banlist");
		sb.append(' ').append("ips");
		final String response = send(sb.toString());
		final int colonPosition = response.indexOf(':');
		final String ipResponse = response.substring(colonPosition + 1).trim();
		final String[] ips = "".equals(ipResponse) ? new String[0] : ipResponse.split(",\\s+");
		return ips;
	}

	public String[] banList() throws IOException, AuthenticationException {
		final StringBuilder sb = new StringBuilder();
		sb.append("banlist");
		final String response = send(sb.toString());
		final int colonPosition = response.indexOf(':');
		final String userResponse = response.substring(colonPosition + 1).trim();
		final String[] users = "".equals(userResponse) ? new String[0] : userResponse.split(",\\s+");
		return users;
	}

	public void close() throws IOException {
		synchronized (syncObject) {
			if (!socket.isClosed()) {
				CompatUtils.safeClose(socket);
			}
		}
	}

	public void deOp(final String player) throws AuthenticationException, IOException {
		final StringBuilder sb = new StringBuilder();
		sb.append("deop");
		sb.append(' ').append(player);
		final String response = send(sb.toString());
		assert "".equals(response);
	}

	public void gameMode(final String player, final GameMode mode) throws AuthenticationException, IOException {
		final StringBuilder sb = new StringBuilder();
		sb.append("gamemode");
		sb.append(' ').append(mode.getNumber());
		final String response = send(sb.toString());
		assert "".equals(response);
	}

	public void give(final String player, final int dataValue) throws AuthenticationException, IOException {
		give(player, dataValue, 1);
	}

	public void give(final String player, final int dataValue, final int amount) throws AuthenticationException, IOException {
		give(player, dataValue, amount, 0);
	}

	public void give(final String player, final int dataValue, final int amount, final int damage) throws AuthenticationException,
			IOException {
		final StringBuilder sb = new StringBuilder();
		sb.append("give");
		sb.append(' ').append(player);
		sb.append(' ').append(dataValue);
		sb.append(' ').append(amount);
		sb.append(' ').append(damage);
		final String response = send(sb.toString());
		assert "".equals(response);
	}

	public void kick(final String player) throws AuthenticationException, IOException {
		final StringBuilder sb = new StringBuilder();
		sb.append("kick");
		sb.append(' ').append(player);
		final String response = send(sb.toString());
		assert "".equals(response);
	}

	public String[] list() throws IOException, AuthenticationException {
		final StringBuilder sb = new StringBuilder();
		sb.append("list");
		final String response = send(sb.toString());
		final int colonPosition = response.indexOf(':');
		final String userResponse = response.substring(colonPosition + 1).trim();
		final String[] users = "".equals(userResponse) ? new String[0] : userResponse.split(",\\s+");
		return users;
	}

	public void op(final String player) throws AuthenticationException, IOException {
		final StringBuilder sb = new StringBuilder();
		sb.append("op");
		sb.append(' ').append(player);
		final String response = send(sb.toString());
		assert "".equals(response);
	}

	public void pardon(final String player) throws AuthenticationException, IOException {
		final StringBuilder sb = new StringBuilder();
		sb.append("pardon");
		sb.append(' ').append(player);
		final String response = send(sb.toString());
		assert "".equals(response);
	}

	public void pardonIp(final String host) throws AuthenticationException, IOException {
		final StringBuilder sb = new StringBuilder();
		sb.append("pardon-ip");
		sb.append(' ').append(host);
		final String response = send(sb.toString());
		assert "".equals(response);
	}

	public void saveAll() throws AuthenticationException, IOException {
		final StringBuilder sb = new StringBuilder();
		sb.append("save-all");
		final String response = send(sb.toString());
		assert "".equals(response);
	}

	public void saveOff() throws AuthenticationException, IOException {
		final StringBuilder sb = new StringBuilder();
		sb.append("save-off");
		final String response = send(sb.toString());
		assert "".equals(response);
	}

	public void saveOn() throws AuthenticationException, IOException {
		final StringBuilder sb = new StringBuilder();
		sb.append("save-on");
		final String response = send(sb.toString());
		assert "".equals(response);
	}

	public void say(final String message) throws AuthenticationException, IOException {
		final StringBuilder sb = new StringBuilder();
		sb.append("say");
		sb.append(' ').append(message);
		final String response = send(sb.toString());
		assert "".equals(response);
	}

	public void stop() throws AuthenticationException, IOException {
		final StringBuilder sb = new StringBuilder();
		sb.append("stop");
		final String response = send(sb.toString());
		assert "".equals(response);
	}

	public void tell(final String player, final String message) throws AuthenticationException, IOException {
		final StringBuilder sb = new StringBuilder();
		sb.append("tell");
		sb.append(' ').append(player);
		sb.append(' ').append(message);
		final String response = send(sb.toString());
		assert "".equals(response);
	}

	public void timeAdd(final int amount) throws AuthenticationException, IOException {
		final StringBuilder sb = new StringBuilder();
		sb.append("time");
		sb.append(' ').append("add");
		sb.append(' ').append(amount);
		final String response = send(sb.toString());
		assert "".equals(response);
	}

	public void timeSet(final int time) throws AuthenticationException, IOException {
		final StringBuilder sb = new StringBuilder();
		sb.append("time");
		sb.append(' ').append("set");
		sb.append(' ').append(time);
		final String response = send(sb.toString());
		assert "".equals(response);
	}

	public void toggleDownfall() throws AuthenticationException, IOException {
		final StringBuilder sb = new StringBuilder();
		sb.append("toggledownfall");
		final String response = send(sb.toString());
		assert "".equals(response);
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("RCon [requestId=");
		builder.append(requestId);
		builder.append(", socket=");
		builder.append(socket);
		builder.append("]");
		return builder.toString();
	}

	public void tp(final String player, final String targetPlayer) throws AuthenticationException, IOException {
		final StringBuilder sb = new StringBuilder();
		sb.append("tp");
		sb.append(' ').append(player);
		sb.append(' ').append(targetPlayer);
		final String response = send(sb.toString());
		assert "".equals(response);
	}

	public String[] whitelist() throws IOException, AuthenticationException {
		final StringBuilder sb = new StringBuilder();
		sb.append("whitelist");
		sb.append(' ').append("list");
		final String response = send(sb.toString());
		final int colonPosition = response.indexOf(':');
		final String userResponse = response.substring(colonPosition + 1).trim();
		final String[] users = "".equals(userResponse) ? new String[0] : userResponse.split(",?\\s+");
		return users;
	}

	public void whitelistAdd(final String player) throws IOException, AuthenticationException {
		final StringBuilder sb = new StringBuilder();
		sb.append("whitelist");
		sb.append(' ').append("add");
		sb.append(' ').append(player);
		final String response = send(sb.toString());
		assert "".equals(response);
	}

	public void whitelistOff() throws IOException, AuthenticationException {
		final StringBuilder sb = new StringBuilder();
		sb.append("whitelist");
		sb.append(' ').append("off");
		final String response = send(sb.toString());
		assert "".equals(response);
	}

	public void whitelistOn() throws IOException, AuthenticationException {
		final StringBuilder sb = new StringBuilder();
		sb.append("whitelist");
		sb.append(' ').append("on");
		final String response = send(sb.toString());
		assert "".equals(response);
	}

	public void whitelistReload() throws IOException, AuthenticationException {
		final StringBuilder sb = new StringBuilder();
		sb.append("whitelist");
		sb.append(' ').append("reload");
		final String response = send(sb.toString());
		assert "".equals(response);
	}

	public void whitelistRemove(final String player) throws IOException, AuthenticationException {
		final StringBuilder sb = new StringBuilder();
		sb.append("whitelist");
		sb.append(' ').append("remove");
		sb.append(' ').append(player);
		final String response = send(sb.toString());
		assert "".equals(response);
	}

	public void xp(final String player, final int amount) throws AuthenticationException, IOException {
		final StringBuilder sb = new StringBuilder();
		sb.append("xp");
		sb.append(' ').append(player);
		sb.append(' ').append(amount);
		final String response = send(sb.toString());
		assert "".equals(response);
	}

	private byte[] send(final int type, final byte[] payload) throws IOException, IncorrectRequestIdException {
		final byte[] receivedPayload;
		synchronized (syncObject) {
			// Send the command.
			final int sendLength = 4 + 4 + payload.length + 2;
			final byte[] sendBytes = new byte[4 + sendLength];
			final ByteBuffer sendBuffer = ByteBuffer.wrap(sendBytes);
			sendBuffer.order(ByteOrder.LITTLE_ENDIAN);
			sendBuffer.putInt(sendLength);
			sendBuffer.putInt(requestId);
			sendBuffer.putInt(type);
			sendBuffer.put(payload);
			sendBuffer.put((byte) 0).put((byte) 0);
			outputStream.write(sendBytes);
			outputStream.flush();

			// Receive the response.
			final byte[] receivedBytes = new byte[2048];
			final int receivedBytesLength = inputStream.read(receivedBytes);
			final ByteBuffer receivedBuffer = ByteBuffer.wrap(receivedBytes, 0, receivedBytesLength);
			receivedBuffer.order(ByteOrder.LITTLE_ENDIAN);
			final int receivedLength = receivedBuffer.getInt();
			final int receivedRequestId = receivedBuffer.getInt();
			@SuppressWarnings("unused")
			final int receivedType = receivedBuffer.getInt();
			receivedPayload = new byte[receivedLength - 4 - 4 - 2];
			receivedBuffer.get(receivedPayload);
			receivedBuffer.get(new byte[2]);
			if (receivedRequestId != requestId) {
				throw new IncorrectRequestIdException(receivedRequestId);
			}
		}
		return receivedPayload;
	}

	private String send(final int type, final String payload) throws IOException, IncorrectRequestIdException {
		return new String(send(type, payload.getBytes(CompatCharsets.UTF_8)), CompatCharsets.UTF_8);
	}

	public String send(final String payload) throws IOException, IncorrectRequestIdException {
		return send(COMMAND_TYPE, payload);
	}

	@Override
	protected void finalize() throws Throwable {
		close();
		super.finalize();
	}
}
