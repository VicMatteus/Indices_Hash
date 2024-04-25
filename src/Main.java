import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main
{
	public static void main(String[] args)
	{
		int chave = 2000;
		int profundidade = 3;
		int indiceHash = chave & ((1 << profundidade) - 1);
		
		DecimalFormat df = new DecimalFormat("000");
		
		Diretorio dir = new Diretorio(3);
		
		System.out.println("Chave: " + chave);
		System.out.println("Profundidade: " + profundidade);
		System.out.println("Índice do bucket: " + indiceHash);
		System.out.println("Índice em binário: " + Integer.toBinaryString(indiceHash));
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