package fi.oulu.tol.group19project.ohap;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.net.http.AndroidHttpClient;
import android.util.Log;

public class OHAPTaskImplementation extends OHAPTaskBase {

	private AndroidHttpClient client = null;

	@Override
	protected void doStart() {
		client = AndroidHttpClient.newInstance("JannasHttpClient");
		HttpParams parameters = client.getParams();
		HttpConnectionParams.setSoTimeout(parameters, 0); // timeout zero means we wait forever.
	}

	@Override
	protected void doStop() {
		client.close();

	}

	protected void prepareAndExecuteRequest(TaskData task) throws InterruptedException {
		//Declare a HttpRequestBase variable and set it to null.
		HttpRequestBase requestBase = null;
		//Using the task parameter, call createUrl (a method you have been given).
		//  * it will return you a URI object (or null if it could not be created)
		//If the returned URI is not null
		//   Call createRequest (a method given to you) with the task and URI objects.
		//   Save the returned request object to the HttpRequestBase variable you declared on the first line
		URI value = createUrl(task);
		if (value != null) {
			requestBase=createRequest(task, value);
		}
		//End if
		//If the request object is null, return away from there
		//   -- we cannot continue 'cause correct request couldn't be created!
		if (requestBase == null) {
			Log.d(threadName(), "cannot continue 'cause correct request couldn't be created!");
			return;
		}
		//Otherwise, we continue:
		//Create a HttpHost object using URI's getHost, getPort and getScheme methods.
		//Set this task object busy at this point, since now we're going to execute the http request and it may take time:
		else {
			Log.d(threadName(), "Crate a HttpHost object");
			HttpHost host = new HttpHost(value.getHost(), value.getPort(), value.getScheme());
			synchronized (this) {
				isBusy = true;
			}
			//Declare a HttpResponse variable and set it to null.
			try {
				HttpResponse response = null;
				//** Now: execute the request using the android http client, with the host and request parameters! **
				Log.d(threadName(), "Execute the request");
				response = client.execute(host, requestBase);
				//We will only get to this line when the server responds
				// -- we may have to wait for seconds, minutes, hours... depending on the server!
				//Then we set this object not busy:
				Log.d(threadName(), "This object not busy");
				synchronized (this) {
					isBusy = false;
				}
				//And now we are ready to check what the server response is...
				//Delcare a HttpEntity object and get that from the response using getEntity().
				Log.d(threadName(), "Check what the server response is");
				HttpEntity entity = response.getEntity();
				//??
				//If the entity is not null
				if (entity != null) {
					//   If the request instace was HttpDelete
					if (requestBase instanceof HttpDelete) {
						//call handleString to close the session after sending the HTTP DELETE
						handleString(TaskData.CLOSE_SESSION_CMD);

					}
					else {
						//   End if
						//   Check the status code of the response (hint: call getStatusLine() of response, then getStatusCode() of statusline).
						//   If status code is HttpStatus.SC_OK then request was completed OK.

						if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {


							//	       Check if the session string is null:
							//	          Means we do not have session yet, so response should contain session id.
							//	          So read the entity's content:

							if (sessionStr == null) {
								BufferedReader rd;


								rd = new BufferedReader(new InputStreamReader(entity.getContent()));
								String line = rd.readLine();
								//	          If line is not null we have the session id
								if (line != null) {
									//Save the line to sessionStr member variable.
									sessionStr = line;
									//call handleString("SESSION " + sessionStr) to notify the protocol about this.
									//Protocol understands the hardcoded "SESSION" to mean that we now got the session id and handles it.
									//See protocol implementation for details
									handleString("SESSION");
								}
								//End if
								//Else (session string is NOT null
								//We do have a session -- that means that we actually got some data from the server!
								//Handle that data by calling handleInputStream with the entitys' content as the parameter


							}
							else {
								handleInputStream(entity.getContent());
							}
						}
					}
				}
			}
			catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}}
		//End if session string is/was null
		//End if status code was SC_OK
		//End if entity was not null
	}

	private HttpRequestBase createRequest(TaskData task, URI Url) {
		HttpRequestBase request = null;
		if (null == sessionStr) {
			if (task.getCommand().equalsIgnoreCase(TaskData.INIT_SESSION_CMD)) {
				// No session, initialize by using just the http address of the server.
				request = new HttpGet(Url);
				Log.d(threadName(), "Creating OHAP session GET (new session)");
			}
		}else {
			String uid = task.getUid();
			if (null == uid) {
				uid = new String("123456");
			}
			String command = task.getCommand();
			Log.d(threadName(), "Command is: " + command);
			if (command.equalsIgnoreCase(TaskData.EMPTY_REQUEST_CMD)) {
				// Session exits, so just get an url with no command and send a post.
				request = new HttpPost(Url);
				Log.d(threadName(), "Creating OHAP empty POST");
			} else if (command.equalsIgnoreCase(TaskData.CLOSE_SESSION_CMD)) {
				request = new HttpDelete(Url);
				Log.d(threadName(), "Creating OHAP session DELETE request");
			} else if (command.equalsIgnoreCase(TaskData.GET_CMD) ||
					command.equalsIgnoreCase(TaskData.SET_CMD) ||
					command.equalsIgnoreCase(TaskData.LISTEN_CMD) ||
					command.equalsIgnoreCase(TaskData.UNLISTEN_CMD)) {
				Log.d(threadName(), "Creating a *POST*");
				request = new HttpPost(Url);
				String content = new String(uid + " " + command);
				String data = task.getData();
				if (null != data) {
					Log.d(threadName(), "Data is: " + data);
					content += " " + data + "\n";
				}
				Log.d(threadName(), "Msg content: " + content);
				StringEntity entity = null;
				try {
					entity = new StringEntity(content);
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
					return null;
				}
				((HttpPost) request).setEntity(entity);
			}
		}
		return request;
	}

	/**
	 * Creates the URL for the http request, based on the task data.
	 * @param task The task to perform, influences on the URL.
	 * @return The URI of the URL generated. Null if could not do it.
	 */
	private URI createUrl(TaskData task) {
		URI Url = null;
		try {
			if (null == sessionStr) {
				if (task.getCommand().equalsIgnoreCase(TaskData.INIT_SESSION_CMD)) {
					// No session, initialize by using just the http address of the server.
					Url = new URI(getServerAddress());
					Log.d(threadName(), "Starting to initiate a OHAP session GET");
				}
			} else {
				Url = new URI(getUrl());
				Log.d(threadName(), "Starting to initiate a OHAP POST");
			}
		} catch (URISyntaxException e) {
			String error = "Malformed URI Exception!!";
			Log.d(threadName(), error);
			e.printStackTrace();
		}
		return Url;
	}
}
