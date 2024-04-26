import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main
{
	public static void main(String[] args) throws IOException
	{
		int chave = 2000;
		int profundidade = 3;
		int indiceHash = chave & ((1 << profundidade) - 1);
		
		DecimalFormat df = new DecimalFormat("000");
		
		Diretorio dir = new Diretorio(3);
		
//		for(int i = 0; i<4; i++)
//		{
//			dir.inlcuirRegistro(new Registro(Integer.toString(i), Integer.toString(1984)));
//		}
		
		dir.inlcuirRegistro(new Registro(Integer.toString(1), Integer.toString(1998)));
		dir.inlcuirRegistro(new Registro(Integer.toString(2), Integer.toString(2024)));
		dir.inlcuirRegistro(new Registro(Integer.toString(3), Integer.toString(1995)));
		
		dir.inlcuirRegistro(new Registro(Integer.toString(4), Integer.toString(2012)));
//		dir.inlcuirRegistro(new Registro(Integer.toString(5), Integer.toString(2013)));
		dir.inlcuirRegistro(new Registro(Integer.toString(5), Integer.toString(2007)));
		dir.inlcuirRegistro(new Registro(Integer.toString(6), Integer.toString(1992)));
		
		dir.inlcuirRegistro(new Registro(Integer.toString(7), Integer.toString(2020)));
		dir.inlcuirRegistro(new Registro(Integer.toString(8), Integer.toString(2013)));
		dir.inlcuirRegistro(new Registro(Integer.toString(9), Integer.toString(2013)));
		dir.inlcuirRegistro(new Registro(Integer.toString(10), Integer.toString(2005)));
		
		
		System.out.println("Chave: " + chave);
//		System.out.println("Profundidade: " + profundidade);
//		System.out.println("Índice do bucket: " + indiceHash);
//		System.out.println("Índice em binário: " + Integer.toBinaryString(indiceHash));
//		try
//		{
//			Bucket bucket = new Bucket("c:/SGBD/teste.txt");
//
//			for(Registro registro : bucket.getRegistros())
//			{
//				System.out.println(registro);
//			}
//		}
//		catch(IOException e)
//		{
//			throw new RuntimeException(e);
//		}
	}
}