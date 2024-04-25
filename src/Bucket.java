import java.io.*;
import java.util.ArrayList;

public class Bucket
{
	private String path;
//	private ArrayList<String> registros;
	private ArrayList<Registro> registros = new ArrayList<Registro>();
	
	public Bucket(String path) throws IOException
	{
		if (!this.validarArquivo(path))
		{
			return;
		}
		this.path = path;
		String[] registroAux;
		//Para iniciar um BUCKET, é necessário passar o caminho, para que seja feita a carga do arquivo para a memória.
		try {
			FileReader arquivo = new FileReader(this.path);
			BufferedReader lerArq = new BufferedReader(arquivo);
			
			String linha = lerArq.readLine(); // lê a primeira linha
			while (linha != null)
			{
				System.out.printf("%s\n", linha);
				registroAux = linha.split(",");
				this.registros.add(new Registro(registroAux[0], registroAux[1]));
				linha = lerArq.readLine(); // lê da segunda até a última linha
			}
			arquivo.close();
		} catch (IOException e) {
			System.out.printf("Erro na abertura do arquivo: %s.\n", e.getMessage());
		}
	}
	
	public String getPath()
	{
		return path;
	}
	public void setPath(String path)
	{
		this.path = path;
	}
	
	public ArrayList<Registro> getRegistros()
	{
		return registros;
	}
//	public void setRegistros(ArrayList<String> registros)
//	{
//		this.registros = registros;
//	}
	
	//A informação que ficará no bucket terá o seguinte formato
	//index na tabela de origem, ano(chave de busca)
	
	//Primeiro tem que buscar o arquivo, ler para memória
	public void incluir(String chave) throws IOException
	{
		int i = 1;
		FileWriter arquivo = new FileWriter("d:\\tabuada.txt");
		PrintWriter gravarArq = new PrintWriter(arquivo);
		
		gravarArq.printf("+--Resultado--+%n");

		gravarArq.printf("+-------------+%n");
		
		arquivo.close();
	}

	//Verifica se o arquivo especificado no path existe. Se não existir, tenta criar. Caso não consiga, retorna false. Outrosim, retorna true.
	private boolean validarArquivo(String path)
	{
		try
		{
			File f = new File(path);
			
			if(f.exists())
			{
				if(f.isFile())
				{
					System.out.printf("\nArquivo (%s) existe - tamanho: %d bytes\n", f.getName(), f.length());
				}
				else
				{
					System.out.print("\nConteúdo do diretório:\n");
				}
				return true;
			}
			else
			{
				System.out.print("Atenção: arquivo ou diretório informado não existe!\nCriando...\n");
				FileWriter arq = new FileWriter(path);
				f = new File(path);
				if(f.exists())
				{
					System.out.println("Arquivo criado.");
					return true;
				}
				else
				{
					System.out.println("Erro ao criar o arquivo.");
					return false;
				}
			}
		}
		catch(Exception e)
		{
			System.err.println(e.getMessage());
		}
		return false;
	}
	
	//Função usada para adicionar um elemento no bucket
	private void addElemento(String novoRegistro)
	{
	
//		this.getRegistros().add(novoRegistro);
	}
}