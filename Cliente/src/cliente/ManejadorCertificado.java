package cliente;
import java.math.BigInteger;
import java.util.Date;

import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v1CertificateBuilder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.crypto.params.RSAPrivateCrtKeyParameters;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.ContentVerifierProvider;
import org.bouncycastle.operator.DefaultDigestAlgorithmIdentifierFinder;
import org.bouncycastle.operator.DefaultSignatureAlgorithmIdentifierFinder;
import org.bouncycastle.operator.bc.BcRSAContentSignerBuilder;
import org.bouncycastle.operator.bc.BcRSAContentVerifierProviderBuilder;

public class ManejadorCertificado {



	AlgorithmIdentifier sigAlgId = new DefaultSignatureAlgorithmIdentifierFinder().find("SHA1withRSA");
	AlgorithmIdentifier digAlgId = new DefaultDigestAlgorithmIdentifierFinder().find(sigAlgId);

	//define lwPrivKey
	RSAKeyParameters lwPubKey = new RSAKeyParameters(
			false,
			new BigInteger("b4a7e46170574f16a97082b22be58b6a2a629798419be12872a4bdba626cfae9900f76abfb12139dce5de56564fab2b6543165a040c606887420e33d91ed7ed7", 16),
			new BigInteger("11", 16));


	RSAPrivateCrtKeyParameters lwPrivKey = new RSAPrivateCrtKeyParameters(
			new BigInteger("b4a7e46170574f16a97082b22be58b6a2a629798419be12872a4bdba626cfae9900f76abfb12139dce5de56564fab2b6543165a040c606887420e33d91ed7ed7", 16),
			new BigInteger("11", 16),
			new BigInteger("9f66f6b05410cd503b2709e88115d55daced94d1a34d4e32bf824d0dde6028ae79c5f07b580f5dce240d7111f7ddb130a7945cd7d957d1920994da389f490c89", 16),
			new BigInteger("c0a0758cdf14256f78d4708c86becdead1b50ad4ad6c5c703e2168fbf37884cb", 16),
			new BigInteger("f01734d7960ea60070f1b06f2bb81bfac48ff192ae18451d5e56c734a5aab8a5", 16),
			new BigInteger("b54bb9edff22051d9ee60f9351a48591b6500a319429c069a3e335a1d6171391", 16),
			new BigInteger("d3d83daf2a0cecd3367ae6f8ae1aeb82e9ac2f816c6fc483533d8297dd7884cd", 16),
			new BigInteger("b8f52fc6f38593dabb661d3f50f8897f8106eee68b1bce78a95b132b4e5b5d19", 16));


	/**
	 * 
	 * @return String con las lineas del certificado, x509 version 3
	 */
	public String creation(){
		try {

			ContentSigner sigGen = new BcRSAContentSignerBuilder(sigAlgId, digAlgId).build(lwPrivKey);


			//
			byte[] publickeyb=sigAlgId.getEncoded();
			//SubjectPublicKeyInfo subPubKeyInfo = ....;
			SubjectPublicKeyInfo subPubKeyInfo = new SubjectPublicKeyInfo( sigAlgId, publickeyb);


			Date startDate = new Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000);
			Date endDate = new Date(System.currentTimeMillis() + 365 * 24 * 60 * 60 * 1000);

			X509v3CertificateBuilder v3CertGen = new X509v3CertificateBuilder(
					new X500Name("CN=EmisorName"), 
					BigInteger.ONE, 
					startDate, endDate, 
					new X500Name("CN=SubjectName"), 
					subPubKeyInfo);

			X509CertificateHolder certHolder = v3CertGen.build(sigGen);

			ContentVerifierProvider contentVerifierProvider = new BcRSAContentVerifierProviderBuilder(
					new DefaultDigestAlgorithmIdentifierFinder())
					.build(lwPubKey);

			if (!certHolder.isSignatureValid(contentVerifierProvider))
			{
				System.err.println("signature invalid");
			}
			
			String msjCert = new String();
			msjCert += certHolder.getVersionNumber() + "\n";
			msjCert += certHolder.getSerialNumber() +"\n";
			msjCert += certHolder.getSignatureAlgorithm() +"\n";
			msjCert += certHolder.getIssuer() +"\n";
			msjCert += certHolder.getNotAfter() +"\n";
			msjCert += certHolder.getSubject() +"\n";
			msjCert += certHolder.getSubjectPublicKeyInfo() +"\n";
			msjCert += certHolder.getIssuer() +"\n";	//deberia ser ID del issuer
			msjCert += certHolder.getSubject() +"\n"; //deberia ser ID del issuer
			msjCert += certHolder.getExtensions();
			System.out.println(msjCert);

			return msjCert;
		} catch (Exception E)
		{
			return null;

		}
		
}

	/**
	 * Devuelve el public key del server
	 * @param lineasCertificadoServer certificado linea por linea
	 * @return llave publica que el servidor pasa dentro del certificado como parametro
	 */
	public String procesarCertificado(String lineasCertificadoServer)
	{
		String[] arrayCertServer = lineasCertificadoServer.split("\n");
		return arrayCertServer[6];
	}
}