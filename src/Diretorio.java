import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;

public class Diretorio
{
	//A profundidade do diretório representa a quantidade de bits menos sig. que serão avaliados como índice.
	protected String basePath = "C:/SGBD";
	protected int globalDepth = 3;
	protected ArrayList<BucketReference> bucketReferences = new ArrayList<BucketReference>();
	private DecimalFormat df = new DecimalFormat("000");
	
	public Diretorio(int globalDepth)
	{
		this.globalDepth = globalDepth;
		String indexAux;
		//Inicializo o diretório com 4 referencias de buckets de 00 a 11.
		//O nome path de cada bucket será o basePath+indice.
		for(int i=0; i<8; i++)
		{
			indexAux = df.format(Integer.parseInt(Integer.toBinaryString(i)));
			bucketReferences.add(new BucketReference(basePath, globalDepth, indexAux, true));
		}
	}
	
	//Ao incluir, o diretório irá passar o hash, avaliar os últimos bits do resultado do hash,
	//verificar no BucketReferencs a qual BucketReferences.getIndice() o registro deve ser inserido.
	//Ao obter o índice, verificar se o bucket possui espaço disponível. Se tiver, já adiciona.
	//Se não tiver espaço, deve-se duplicar o diretório(COMO IMPLEMENTAR ISSO?)
	//Caso um bucket tenha a profundidade local = profundidade global e seja dividido, sabemos que o diretório
	//deverá ser duplicado.
	//Caso o bucket seja dividido, a profundidade local deve ser incrementada e a profundidade local da imagem
	//deve ser igual a da origem.
	
	//Sobre a duplicação do diretório, trata de duplicar, literalmente o BucketReferences, e percorrer para cada um destes
	//indice: devo aumentar o número de bits significativos, acredito que apenas fazendo a concatenação de "1" + indice antigo sirva.
	//path: Os arquivos antigos não serão modificados, ficando como seus paths anteriores. Já os novos terão seu caminho descriminado pela
	//      concatenação do path+indice.
	//localDepth: Acrescentar um a isso.
	
	public void inlcuirRegistro(Registro registro) throws IOException
	{
		String retorno = "";
		//esse valor vai me indicar qual referencia acessar
		String index = hash(registro.getConteudo());
		//Busco nas refs de bucket pelo 'indice' resultado do h(key).
		BucketReference bucketAux = buscarReferencia(index);
		//Agora tenho acesso rápido path e localDepth
		
		//Agora, preciso instanciar o bucket na memória para entender como ele está.
		
		Bucket bucket = new Bucket(bucketAux.getPath());
		if(bucket.getRegistros().size() < 3)
		{
			//Realizo a escrita da linha no arquivo
			bucket.addRegister(registro);
			retorno = "INC:"+registro.getConteudo()+"/"+this.globalDepth+","+bucketAux.getLocalDepth()+"\n";
		}
		else
		{
			//Preciso duplicar o bucket e verificar se precisarei duplicar o diretório
			//Se o bucket atual está cheio e sua profundidade é igual a do diretório, não posso duplica-lo sem ter
			//sua referencia, então duplico o diretório.
			if(bucketAux.getLocalDepth() == this.globalDepth)
			{
				duplicarDiretorio();
				//Se duplicar, preciso refazer hash?
				//Atualiza o hash com os digitos significativos para a nova profundidade
				String indexAntigo = df.format(Integer.parseInt(index));//indice do destino antigo
				index = hash(registro.getConteudo());//indice de destino atual
			}
			
			//Adicionou o arquivo do bucket nas referencias e ativo ele
			addBucket(index);
			
			ArrayList<Registro> prevRegistroAux = new ArrayList<Registro>();
			ArrayList<Registro> moveRegistroAux = new ArrayList<Registro>();
			
			//Redistribuir os registros no bucket antigo lotado
			for(Registro rg : bucket.getRegistros())
			{
				String IndexAux = df.format(Integer.parseInt(hash(rg.getConteudo()))); //hash novamente o registro antigo para atualizazr o indice
				if(!IndexAux.equals(index))
				{
					prevRegistroAux.add(rg);
				}
				else
				{
					moveRegistroAux.add(rg);
				}
			}
			bucket.setRegistros(prevRegistroAux);
			
			bucketAux = buscarReferencia(index);; //Transforma string binária em decimal
			
			//Trago o novo bucket para a memória
			bucket = new Bucket(bucketAux.getPath());
			
			moveRegistroAux.add(registro);
			bucket.setRegistros(moveRegistroAux);
			retorno = "INC:"+registro.getConteudo()+"/"+this.globalDepth+","+bucketAux.getLocalDepth()+"\n";
		}
		gravarOutput(retorno);
	}
	
	//Aplica o hash e retorna a String contendo os últimos digitos mais significativos baseado na profundidade
	public String hash(String key)
	{
		return Integer.toBinaryString(Integer.parseInt(key) & ((1 << globalDepth) - 1));
	}
	
	public ArrayList<BucketReference> getBucketReferences()
	{
		return bucketReferences;
	}
	
	//Função responsável por duplicar o diretório, expandindo suas referências e aumentando a profundidade global.
	private void duplicarDiretorio()
	{
		int qtdBuckets = this.bucketReferences.size();
		String indexAux = "";
		
		//Vai da quantidade atual de buckets até o odbro dessa quantidade.
		for(int i = qtdBuckets; i < 2*qtdBuckets; i++)
		{
			//inteiro para binário com 4 caracteres
			indexAux = df.format(Integer.parseInt(Integer.toBinaryString(i)));//Inteiro para binario para inteiro para string binária com 4 posições
			
			//Teoricamente, duplico a quantidade de referencias apontando para os mesmos buckets correspondentes
			//(x)Eles apontarão para novos buckets quando um novo bucket for, de fato, criado.
			//Duplico as referencias com profundidade local mantida. Será atualizada apenas na duplicação de bucket.
			this.bucketReferences.add(new BucketReference(basePath, globalDepth, indexAux, false));//bucketReferences.get(i).indice
		}
		this.globalDepth = this.globalDepth+1;
		df = new DecimalFormat(String.join("", Collections.nCopies(this.globalDepth, "0")));
	}
	
	//Apenas será necessario adicionar um bucket quando o bucket anteiror estiver cheio e com diretório disponível para apontamento.
	//Se a profundidade global for maior que a local, então
	private void addBucket(String indexAux) throws IOException
	{
		//IndexAux deve me mostrar qual bucket deve ser duplicado.
		BucketReference br = buscarReferencia(indexAux);;//transforma binário em decimal
		br.ativar();
	}
	
	public BucketReference buscarReferencia(String indiceAlvo)
	{
		indiceAlvo = df.format(Integer.parseInt(indiceAlvo));
		for(BucketReference br : this.bucketReferences)
		{
			if(br.getIndice().equals(indiceAlvo))
			{
				return br;
			}
		}
		return new BucketReference("", 0, "", false);
	}
	
	public void buscaIgualdade(String chaveAlvo) throws IOException
	{
//		String indice = df.format(Integer.parseInt(hash(chaveAlvo)));
		String indice = hash(chaveAlvo);
		String retorno = "";
		int tuplasSelecionadas = 0;
		
		BucketReference br = buscarReferencia(indice);
		Bucket bucket = new Bucket(br.getPath());
		
		for(Registro registro : bucket.getRegistros())
		{
			if(registro.getConteudo().equals(chaveAlvo))
			{
				tuplasSelecionadas++;
			}
		}
		retorno += "BUS:" + chaveAlvo + "/" + tuplasSelecionadas;
		gravarOutput(retorno);
	}
	
	public void rmRegistro(String chaveAlvo) throws IOException
	{
		String indice = hash(chaveAlvo);
		String retorno = "";
		int tuplasSelecionadas = 0;
		
		BucketReference br = buscarReferencia(indice);
		Bucket bucket = new Bucket(br.getPath());
		ArrayList<Registro> registros = new ArrayList<Registro>();
		
		for(Registro registro : bucket.getRegistros())
		{
			if(!registro.getConteudo().equals(chaveAlvo))
			{
				registros.add(registro);
			}
			else
				tuplasSelecionadas++;
		}
		bucket.setRegistros(registros);
		retorno += "REM:" + chaveAlvo + "/" + tuplasSelecionadas +","+ this.globalDepth +","+ br.getLocalDepth();
		gravarOutput(retorno);
	}
	
	public void gravarOutput(String conteudo) throws IOException
	{
		FileWriter arquivo = new FileWriter("C:/SGBD/out.txt", true);
		PrintWriter escritor = new PrintWriter(arquivo, true);
		
		escritor.write(conteudo+"\n");
		
		arquivo.close();
	}
}

