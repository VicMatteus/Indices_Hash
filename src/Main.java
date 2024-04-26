import java.io.*;
import java.text.DecimalFormat;
import java.util.ArrayList;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main
{
	public static void main(String[] args) throws IOException
	{
		String pathCSV = "C:/SGBD/compras.csv";
		String[] registroAux;
		ArrayList<Registro> registros = new ArrayList<Registro>();
		
		//Varredura inicial do csv
		try {
			FileReader arquivo = new FileReader(pathCSV);
			BufferedReader lerArq = new BufferedReader(arquivo);
			
			String linha = lerArq.readLine();
			while (linha != null)
			{
				linha = linha.replace(";", "");
				registroAux = linha.split(",");
				registros.add(new Registro(registroAux[0], registroAux[2]));
				linha = lerArq.readLine(); // lê da segunda até a última linha
			}
			arquivo.close();
		} catch (IOException e) {
			System.out.printf("Erro na abertura do arquivo: %s.\n", e.getMessage());
		}
		
		DecimalFormat df = new DecimalFormat("000");
		
		Diretorio dir = new Diretorio(3);
		
		boolean x = true;
		if (x)
		{
			for(Registro rg : registros)
			{
				dir.inlcuirRegistro(rg);
			}
		}
		
		dir.inlcuirRegistro(new Registro("11", "1992"));
		dir.buscaIgualdade("1992");
		dir.rmRegistro("1992");
		dir.inlcuirRegistro(new Registro("11", "1992"));
		
//		dir.inlcuirRegistro(new Registro(Integer.toString(1), Integer.toString(1998)));
//		dir.inlcuirRegistro(new Registro(Integer.toString(2), Integer.toString(2024)));
//		dir.inlcuirRegistro(new Registro(Integer.toString(3), Integer.toString(1995)));
//
//		dir.inlcuirRegistro(new Registro(Integer.toString(4), Integer.toString(2012)));
//		dir.inlcuirRegistro(new Registro(Integer.toString(5), Integer.toString(2007)));
//		dir.inlcuirRegistro(new Registro(Integer.toString(6), Integer.toString(1992)));
//
//		dir.inlcuirRegistro(new Registro(Integer.toString(7), Integer.toString(2020)));
//		dir.inlcuirRegistro(new Registro(Integer.toString(8), Integer.toString(2013)));
//		dir.inlcuirRegistro(new Registro(Integer.toString(9), Integer.toString(2013)));
//		dir.inlcuirRegistro(new Registro(Integer.toString(10), Integer.toString(2005)));
	}
}