import java.io.*;
import java.text.DecimalFormat;
import java.util.ArrayList;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main
{
	public static void main(String[] args) throws IOException
	{
		String[] auxOperator;
		ArrayList<Registro> registros = new ArrayList<Registro>();
		String pathInTxt = "C:/SGBD/inout/in.txt";
		int profundidadeGlobal = 0;
		Diretorio dir;
		//Leitura das instruções do in.txt
		try
		{
			FileReader arquivo = new FileReader(pathInTxt);
			BufferedReader lerArq = new BufferedReader(arquivo);
			
			String linha = lerArq.readLine();
			if(linha == null)
			{
				return;
			}
			
			//Pega a partir de PG/
			profundidadeGlobal = Integer.parseInt(linha.substring(3));
			dir = new Diretorio(profundidadeGlobal);
			linha = lerArq.readLine();
			
			while(linha != null)
			{
				//Identifica a operação e a chave
				auxOperator = linha.split(":");
				
				//realiza a operação
				switch(auxOperator[0])
				{
					case "INC":
						//busca no arquivo
						registros = scanFile(auxOperator[1]);
						
						//Realiza a aoperação
						for(Registro registro : registros)
						{
							dir.inlcuirRegistro(registro);
						}
						break;
					
					case "BUS":
						dir.buscaIgualdade(auxOperator[1]);
						break;
						
					case "REM":
						dir.rmRegistro(auxOperator[1]);
						break;
					default:
						System.out.println("Operação não autorizada: " + auxOperator[0]);
						return;
				}
				
				linha = lerArq.readLine(); // lê da segunda até a última linha
			}
			arquivo.close();
			dir.gravarOutput("PG/" + Integer.toString(dir.getGlobalDepth()));
		}
		catch(IOException e)
		{
			System.out.printf("Erro na abertura do arquivo: %s.\n", e.getMessage());
		}
		
		
//		DecimalFormat df = new DecimalFormat("000");
//
//		Diretorio dir = new Diretorio(2);
		
		
		//		dir.inlcuirRegistro(new Registro("11", "1992"));
		//		dir.buscaIgualdade("1992");
		//		dir.rmRegistro("1992");
		//		dir.inlcuirRegistro(new Registro("11", "1992"));
		
		//		dir.inlcuirRegistro(new Registro(Integer.toString(1), Integer.toString(1998)));
		//		dir.inlcuirRegistro(new Registro(Integer.toString(2), Integer.toString(2024)));
		//		dir.inlcuirRegistro(new Registro(Integer.toString(3), Integer.toString(1995)));
		//		dir.inlcuirRegistro(new Registro(Integer.toString(4), Integer.toString(2012)));
		//		dir.inlcuirRegistro(new Registro(Integer.toString(5), Integer.toString(2007)));
		//		dir.inlcuirRegistro(new Registro(Integer.toString(6), Integer.toString(1992)));
		//		dir.inlcuirRegistro(new Registro(Integer.toString(7), Integer.toString(2020)));
		//		dir.inlcuirRegistro(new Registro(Integer.toString(8), Integer.toString(2013)));
		//		dir.inlcuirRegistro(new Registro(Integer.toString(9), Integer.toString(2013)));
		//		dir.inlcuirRegistro(new Registro(Integer.toString(10), Integer.toString(2005)));
		//		dir.inlcuirRegistro(new Registro(Integer.toString(11), Integer.toString(2021)));
		//		dir.inlcuirRegistro(new Registro(Integer.toString(12), Integer.toString(0)));
		//		dir.inlcuirRegistro(new Registro(Integer.toString(13), Integer.toString(0)));
		//		dir.inlcuirRegistro(new Registro(Integer.toString(14), Integer.toString(8)));
		//
		//		dir.buscaIgualdade("2013");
		//		dir.rmRegistro("2013");
		//		dir.buscaIgualdade("2013");
		
	}
	
	static ArrayList<Registro> scanFile(String chaveAlvo)
	{
		String pathCSV = "C:/SGBD/compras.csv";
		String[] registroAux;
		ArrayList<Registro> registros = new ArrayList<Registro>();
		
		try
		{
			FileReader arquivo = new FileReader(pathCSV);
			BufferedReader lerArq = new BufferedReader(arquivo);
			
			String linha = lerArq.readLine();
			while(linha != null)
			{
				linha = linha.replace(";", "");
				registroAux = linha.split(",");
				
				//O registro no csv é igual ao valor a receber a operação
				if(registroAux[2].equals(chaveAlvo))
				{
					registros.add(new Registro(registroAux[0], registroAux[2]));
				}
				linha = lerArq.readLine(); // lê da segunda até a última linha
			}
			arquivo.close();
		}
		catch(IOException e)
		{
			System.out.printf("Erro na abertura do arquivo: %s.\n", e.getMessage());
		}
		return registros;
	}
	
}
