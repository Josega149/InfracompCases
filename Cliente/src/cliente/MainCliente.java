package cliente;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.KeyPair;
import java.security.PublicKey;

public class MainCliente 
{
	private Socket canal;
	private PrintWriter out;
	private BufferedReader in;

	private final static String DIRECCION = "localhost";
	private final static int PUERTO = 4443;

	private CifradorSimetricoAES cifradorSim;
	private CifradorAsimetricoRSA cifradorAsim;
	private KeyPair keyAsin;
	
	private CifradorHmacMD5 cifradorHash;
	private ManejadorCertificado manejadorCertificado;

	public MainCliente()
	{
		//crea el cifrador asimetrico y la llave publica
		cifradorAsim = new CifradorAsimetricoRSA();
		keyAsin = cifradorAsim.darLlave();

		//crea el cifrador simetrico 
		cifradorSim = new CifradorSimetricoAES();

		//crea el hash
		cifradorHash = new CifradorHmacMD5();
		
		//crea el manejador de certificado
		manejadorCertificado = new ManejadorCertificado();
		
		//inicia el protocolo de comunicacion
		iniciarComunicacion();

	}

	public void iniciarComunicacion()
	{
		// conectar al servidor
		try 
		{
			canal = new Socket(DIRECCION, PUERTO);
			out = new PrintWriter(canal.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(canal.getInputStream()));

			iniciarConversacion();
		}catch (Exception e)
		{
			e.getStackTrace();
		}
	}
	public void iniciarConversacion() throws Exception
	{
		out.println("HOLA");
		String resp = in.readLine();
		if(!resp.equals("OK")){throw new Exception("SERVIDOR REPONDIO MAL (el hola)");}
		
		out.println("ALGORITMOS:AES:RSA:HMACMD5");
		resp = in.readLine();
		if(resp.equals("OK") || resp.equals("ERROR"))
		{
			if(resp.equals("ERROR")){throw new Exception("SACO ERROR POR LOS ALGORITMOS");}
		}else{throw new Exception("SERVIDOR REPONDIO MAL (ni ok ni error para algoritmos)");}
		
		// comienzo pasar el certificado
		manejadorCertificado.enviarCertificado();
		resp = in.readLine();
		if(!resp.equals("CERTIFICADOSERVIDOR")){throw new Exception ("SERVIDOR RESPONDIO MAL (el certificado)");}
		
		out.println("OK");
		resp = in.readLine();//llega cifrado con la llave publica del cliente (la mia)
		byte [] textoEnBytes = resp.getBytes();
		String llaveSimetricaAcordada = cifradorAsim.descifrarLlaveSimetrica(textoEnBytes, keyAsin.getPrivate());
		
		
		//mando cifrado con la llave publica del server la llave que me llego
		byte [] llaveSimetricaAcordadaB = llaveSimetricaAcordada.getBytes();
		String llaveSimetricaCifrada = cifradorAsim.cifrarLlaveSimetrica(llaveSimetricaAcordadaB, llavePublicaServer);
		out.println(llaveSimetricaCifrada);
	
		resp = in.readLine();
		if(!resp.equals("OK")){throw new Exception ("SERVIDOR RESPONDIO MAL (el OK despues de mandar la llave simetrica)");}
		
		
		
		
		out.println("CIFRADOLS1");
		resp = in.readLine();
		if(!resp.equals("CIFRADOLS2")){throw new Exception ("SERVIDOR RESPONDIO MAL (el CIFRADOLS2");}
		
		System.out.println("TERMINA!");
	}


		public static void main(String[] args) 
		{
			MainCliente main = new MainCliente();
		}
	}
