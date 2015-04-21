package com.boha.ghostpractice.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.net.URLEncoder;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.http.util.ByteArrayBuffer;

import android.content.Context;
import android.provider.Settings;
import android.util.Log;

import com.boha.ghostpractice.data.GhostRequestDTO;
import com.boha.ghostpractice.data.WebServiceResponse;
import com.boha.ghostpractice.util.bean.CommsException;
import com.google.gson.Gson;

public class CommsUtil {

	public static String getZippedData(String request) throws CommsException {
		HttpURLConnection con = null;
		URL url;
		String response = null;
		InputStream is = null;
		try {
			url = new URL(request);
			con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("GET");
			// Start the query
			con.connect();
			is = con.getInputStream();
			ZipInputStream zis = new ZipInputStream(new BufferedInputStream(is));
			@SuppressWarnings("unused")
			ZipEntry entry = null;
			ByteArrayBuffer bab = new ByteArrayBuffer(2048);
			while ((entry = zis.getNextEntry()) != null) {
				int size = 0;
				byte[] buffer = new byte[2048];
				while ((size = zis.read(buffer, 0, buffer.length)) != -1) {
					bab.append(buffer, 0, size);
				}

			}
			response = new String(bab.toByteArray());
			int code = con.getResponseCode();
			Log.i(COMMS, "Comms(zipped) HTTP response code: " + code
					+ " \nresp: " + response);
		} catch (IOException e) {
			Log.e(COMMS, "Houston, we have a problem - IOException issues..");
			Log.d(COMMS, "Request in error: \n" + request);
			throw new CommsException(CommsException.CONNECTION_ERROR);
		} finally {
			try {
				is.close();
			} catch (Exception e) {
				// Log.e(COMMS, "Unable to close input stream, but no prob!");
			}
		}
		return response;
	}

	public static void postElapsedTime(int activityID, double elapsedSeconds,
			Context ctx) throws CommsException, NetworkUnavailableException {
		Gson gson = new Gson();

		WebServiceResponse resp = null;
		GhostRequestDTO req = new GhostRequestDTO();
		req.setRequestType(GhostRequestDTO.POST_DEVICE_ELAPSED_TIME);
		req.setDeviceElapsedSeconds(elapsedSeconds);
		req.setActivityID(activityID);

		String json = URLEncoder.encode(gson.toJson(req));
		resp = CommsUtil.getData(Statics.URL + json, ctx);
		Log.i(COMMS, "## Elapsed device time posted: " + elapsedSeconds
				+ ", response code: " + resp.getResponseCode());
	}

	public static WebServiceResponse getData(String request, Context ctx)
			throws CommsException, NetworkUnavailableException {
		Log.d(COMMS, "sending request: .......\n" + request);
		HttpURLConnection con = null;
		// Check if there's a proxy set up for internet access
		String heita = Settings.Secure.getString(ctx.getContentResolver(),
				Settings.Secure.HTTP_PROXY);
		Log.i(COMMS, "##### proxy on device: " + heita);

		URL url = null;
		String response = null;
		InputStream is = null;
		WebServiceResponse webResp = null;
		// check network
		WebCheckResult res = WebCheck.checkNetworkAvailability(ctx);
		if (res.isNetworkUnavailable()) {
			throw new NetworkUnavailableException();
		}
		try {
			url = new URL(request);
			if (heita == null) {
				con = (HttpURLConnection) url.openConnection(); // There is no
																// proxy defined
			} else {
				int ix = heita.lastIndexOf(":");
				String p = heita.substring(ix + 1);
				try {
					int port = Integer.parseInt(p);
					String host = heita.substring(0, ix);
					Proxy proxy = new Proxy(Proxy.Type.HTTP,
							new InetSocketAddress(host, port));
					con = (HttpURLConnection) url.openConnection(proxy);
					Log.d(COMMS, "Got connection using proxy: " + heita);
				} catch (Exception e) {
					Log.e(COMMS, "*** Error trying to get proxy from device", e);
					con = (HttpURLConnection) url.openConnection();
				}
			}
			con.setRequestMethod("GET");
			con.connect();
			is = con.getInputStream();
			int httpCode = con.getResponseCode();
			String msg = con.getResponseMessage();
			Log.d(COMMS, "### HTTP response code: " + httpCode + " msg: " + msg);
			response = readStream(is);
			// check for html
			int idx = response.indexOf("DOCTYPE html");
			if (idx > -1) {
				Log.e(COMMS, "@@@ ERROR RESPONSE, some html received:\n"
						+ response);
				throw new NetworkUnavailableException();
			}
			Gson gson = new Gson();
			webResp = gson.fromJson(response, WebServiceResponse.class);
			int code = con.getResponseCode();
			if (webResp != null) {
				Log.i(COMMS, "Comms HTTP code: " + code
						+ " back-end respCode: " + webResp.getResponseCode());
			} else {
				Log.e(COMMS,
						"&&&&&&& ++ It's a Houston kind of problem: json deserializer returned null");
				webResp = new WebServiceResponse();
				webResp.setResponseCode(9999);
				webResp.setResponseMessage("Funny problem, Haha! response is NULL!");
			}

		} catch (IOException e) {
			Log.e(COMMS, "Houston, we have an IOException. F%$%K!, url: " + url.toString(), e);
			throw new CommsException(CommsException.CONNECTION_ERROR);

		} finally {
			try {
				is.close();
			} catch (Exception e) {
				// Log.e(COMMS,
				// "Unable to close input stream - should be no problem.");
			}
		}

		return webResp;
	}

	public static String postData(String urlReq, String request)
			throws CommsException {
		HttpURLConnection con = null;
		URL url;
		String response = null;
		InputStream is = null;
		try {
			url = new URL(urlReq);
			con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("POST");
			con.setDoOutput(true);
			con.setFixedLengthStreamingMode(request.getBytes().length);
			OutputStreamWriter wr = new OutputStreamWriter(
					con.getOutputStream());
			wr.write(request);
			wr.flush();
			// Start the query
			con.connect();
			is = con.getInputStream();
			response = readStream(is);
			int code = con.getResponseCode();
			Log.i(COMMS, "Comms HTTP POST response code: " + code);
		} catch (IOException e) {
			throw new CommsException(CommsException.CONNECTION_ERROR);
		} finally {
			try {
				is.close();
			} catch (Exception e) {
				Log.e(COMMS, "Unable to close input stream");
			}
		}
		return response;
	}

	public static String readStream(InputStream in) throws IOException {
		StringBuilder sb = new StringBuilder();
		BufferedReader r = new BufferedReader(new InputStreamReader(in), 1024);
		for (String line = r.readLine(); line != null; line = r.readLine()) {
			sb.append(line);
		}
		in.close();
		return sb.toString();
	}

	private static final String COMMS = "CommsUtil";
}
