package lab1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class ActiveClient extends MessageParser implements Runnable {

	public static String MonitorName;
	Thread runner;
	Socket toMonitor = null;
	public static int MONITOR_PORT;
	public static int LOCAL_PORT;
	public int SleepMode;
	int DELAY = 90000; // Interval after which a new Active Client is started
	long prevTime, present;

	public ActiveClient() {
		super("[no-name]", "[no-password]");
		MonitorName = "";
		toMonitor = null;
		MONITOR_PORT = 0;
		LOCAL_PORT = 0;
	}

	public ActiveClient(String mname, int p, int lp, int sm, String name, String password) {
		super(name, password);
		try {
			SleepMode = sm;
			MonitorName = mname;
			MONITOR_PORT = p;
			LOCAL_PORT = lp;
		} catch (NullPointerException n) {
			System.out.println("Active Client [Constructor]: TIMEOUT Error: " + n);
		}
	}

	public void start() {
		if (runner == null) {
			runner = new Thread(this);
			runner.start();
		}
	}

	public void run() {
		while (Thread.currentThread() == runner) {
			try {
				System.out.print("Active Client: trying monitor: " + MonitorName + " port: " + MONITOR_PORT + "...");
				toMonitor = new Socket(MonitorName, MONITOR_PORT);
				System.out.println("completed.");
				out = new PrintWriter(toMonitor.getOutputStream(), true);
				in = new BufferedReader(new InputStreamReader(toMonitor.getInputStream()));
//				HOSTNAME = toMonitor.getLocalAddress().getHostName();
				HOSTNAME = toMonitor.getLocalAddress().toString().substring(1);
				System.out.println(HOSTNAME);
				SERVER_HOSTNAME = "129.137.96.20";
				CType = 0; // Indicates Client
				HOST_PORT = LOCAL_PORT;
				if (!Login()) {
					if (IsVerified == 0)
						System.exit(1);
				}
				System.out.println("***************************");
				if (Execute("GET_GAME_IDENTS")) {
					String msg = GetMonitorMessage();
					System.out.println("ActiveClient [GET_GAME_IDENTS]:\n\t" + msg);
				}
				if (Execute("RANDOM_PARTICIPANT_HOST_PORT")) {
					String msg = GetMonitorMessage();
					System.out.println("ActiveClient [RANDOM_PARTICIPANT_HOST_PORT]:\n\t" + msg);
				}
				if (Execute("PARTICIPANT_HOST_PORT", "FRANCO")) {
					String msg = GetMonitorMessage();
					System.out.println("ActiveClient [PARTICIPANT_HOST_PORT]:\n\t" + msg);
				}
				if (Execute("PARTICIPANT_STATUS")) {
					String msg = GetMonitorMessage();
					System.out.println("ActiveClient [PARTICIPANT_STATUS]:\n\t" + msg);
				}
				ChangePassword(PASSWORD);
				System.out.println("Password:" + PASSWORD);

				toMonitor.close();
				out.close();
				in.close();
				try {
					runner.sleep(DELAY);
				} catch (Exception e) {
				}

			} catch (UnknownHostException e) {
			} catch (IOException e) {
				e.printStackTrace();
				try {
					toMonitor.close();
					// toMonitor = new Socket(MonitorName,MONITOR_PORT);
				} catch (IOException ioe) {
				} catch (NullPointerException n) {
					n.printStackTrace();
					try {
						toMonitor.close();
						// toMonitor = new Socket(MonitorName,MONITOR_PORT);
					} catch (IOException ioe) {
					}
				}
			}
		}
	}

	/*public boolean Login() {
		boolean success = false;
		try {
			out.println("IDENT FRANCO");
			
			 * Server server = new Server(MONITOR_PORT, LOCAL_PORT, IDENT,
			 * filename); server.start();
			 
			if (out.checkError() == true)
				throw (new IOException());
			out.flush();
			if (out.checkError() == true)
				throw (new IOException());
			String command1 = in.readLine();
			String command2 = in.readLine();
			String command3 = in.readLine();
			System.out.println("line-1" + command1);
			System.out.println("line-2" + command2);
			System.out.println("line-3" + command3);
			success = true;
		} catch (NullPointerException n) {
			System.out.println("MessageParser [Login]: null pointer error " + "at login:\n\t" + n);
			success = false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("Success Value Login = " + success);
		return success;
	}*/
}
