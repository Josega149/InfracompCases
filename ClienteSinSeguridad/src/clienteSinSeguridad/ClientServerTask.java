package clienteSinSeguridad;

import uniandes.gload.core.Task;

public class ClientServerTask extends Task{

	@Override
	public void fail() {
		// TODO Auto-generated method stub
		System.out.println(Task.MENSAJE_FAIL);
	}

	@Override
	public void success() {
		// TODO Auto-generated method stub
		System.out.println(Task.OK_MESSAGE);
	}

	@Override
	public void execute() {
		// TODO Auto-generated method stub
		
		try {
			@SuppressWarnings("unused")
			MainClienteSinSeguridad cliente = new MainClienteSinSeguridad();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
}
