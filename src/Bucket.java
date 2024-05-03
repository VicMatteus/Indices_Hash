import java.io.*;
import java.util.ArrayList;

/*
* Possui um array de registros lidos do arquivo fisico e um caminho(ponteiro)
*/
public class Bucket
{
	private String path;
	private ArrayList<Registro> registros = new ArrayList<Registro>();
	
	public Bucket(String path) throws IOException
	{
		//Valida se o arquivo existe. Se não, tenta criar.
		if (!this.validarArquivo(path))
		{
			return;
		}
		this.path = path;
		String[] registroAux;
		//Para iniciar um BUCKET, é necessário passar o caminho do arq físico, para que seja feita a carga do arquivo para a memória.
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
	public void setRegistros(ArrayList<Registro> registros) throws IOException
	{
		this.registros = registros;
		gravarArquivo();
	}
	
	//A informação que ficará no bucket terá o seguinte formato
	//index na tabela de origem, ano(chave de busca)
	
	//Primeiro tem que buscar o arquivo, ler para memória
	
	//função que fará a gravação dos registros no arquivo.
	public void gravarArquivo() throws IOException
	{
		FileWriter arquivo = new FileWriter(this.path);
		PrintWriter escritor = new PrintWriter(arquivo);
		
		for(Registro registro : this.registros)
		{
			escritor.printf(registro.toString()+"\n");
		}
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
	public void addRegister(Registro registro) throws IOException
	{
		this.registros.add(registro);
		gravarArquivo();
	}
	//Função usada para remover um elemento no bucket
	public void rmRegister(Registro registro) throws IOException
	{
		this.registros.remove(registro);
		gravarArquivo();
	}
}