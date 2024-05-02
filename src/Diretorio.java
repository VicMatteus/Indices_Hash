import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.GregorianCalendar;
/*
	Ao incluir, o diretório irá passar o hash, avaliar os últimos bits do resultado do hash,
	verificar no BucketReferencs a qual BucketReferences.getIndice() o registro deve ser inserido.
	Ao obter o índice, verificar se o bucket possui espaço disponível. Se tiver, já adiciona.
	Se não tiver espaço, deve-se duplicar o diretório(COMO IMPLEMENTAR ISSO?)
	Caso um bucket tenha a profundidade local = profundidade global e seja dividido, sabemos que o diretório
	deverá ser duplicado.
	Caso o bucket seja dividido, a profundidade local deve ser incrementada e a profundidade local da imagem
	deve ser igual a da origem.
	
	Sobre a duplicação do diretório, trata de duplicar, literalmente o BucketReferences, e percorrer para cada um destes
	indice: devo aumentar o número de bits significativos, acredito que apenas fazendo a concatenação de "1" + indice antigo sirva.
	path: Os arquivos antigos não serão modificados, ficando como seus paths anteriores. Já os novos terão seu caminho descriminado pela
	      concatenação do path+indice.
	localDepth: Acrescentar um a isso.
 */

public class Diretorio
{
	protected String basePath = "C:/SGBD";
	protected int globalDepth = 2;
	protected ArrayList<BucketReference> bucketReferences = new ArrayList<BucketReference>();
	private   DecimalFormat df = new DecimalFormat("0000");
	
	//Cria 8 referências partindo de 000 a 111 e as adiciona no atributo.
	public Diretorio(int globalDepth)
	{
		this.globalDepth = globalDepth;
		String indexAux;
		//Inicializo o diretório com 8 referencias de buckets de 000 a 111.
		//O path de cada bucket será o basePath+indice.
		for(int i=0; i<2*this.globalDepth; i++)
		{
			indexAux = formatarStringBinaria(i);
			bucketReferences.add(new BucketReference(basePath, globalDepth, indexAux, true));
		}
	}
	
	
	public ArrayList<BucketReference> getBucketReferences()
	{
		return bucketReferences;
	}
	public int getGlobalDepth() { return this.globalDepth; }
		
	public void inlcuirRegistro(Registro register) throws IOException
	{
		int i = 0;
		String retorno       = "";
		String indexAux      = "";
		String previousIndex = "";
		BucketReference bucketRef;
		String index;
		//Busco nas refs de bucket pelo 'indice' resultado do h(key).
		do
		{
			index = hash(register.getConteudo(), globalDepth-i);
			bucketRef = buscarReferencia(index);
			i++;
		} while(!bucketRef.isAtivo());
			
		//Agora, preciso instanciar o bucket na memória para entender como ele está.
		Bucket bucket = new Bucket(bucketRef.getPath());
		
		//Caso tenha mais de 3 registros, será necessário aumentar os buckets
		if(bucket.getRegistros().size() < 3)
		{
			//Realizo a escrita da linha no arquivo
			bucket.addRegister(register);
			retorno = "INC:"+register.getConteudo()+"/"+this.globalDepth+","+bucketRef.getLocalDepth()+"\n";
		}
		else
		{
			//Se o bucket atual está cheio e sua profundidade é igual a do diretório, não posso duplica-lo sem ter sua referencia, então duplico o diretório.
			if(bucketRef.getLocalDepth() == this.globalDepth)
			{
				duplicarDiretorio();
				//Atualiza o hash com os digitos significativos para a nova profundidade
				previousIndex = index;//index anterior a atualização da profundidade global
				
				//index anterior a atualização, sendo atualizado com o global.
				index = hash(register.getConteudo());//pode ser q o registro que eu queira inserir tenha o mesmo hash. como identificar o indice do
				// que eu acabei de dobrar?
			}
			else
				previousIndex = index;
			
			//Esse cara é o índice do bucket que está cheio, precisando se dividir.
			String previousFullBucketIndex = bucketRef.getIndice();
			
			//Esse cara é o número de bits que devem ser levados em consideração no indice...
			int previousFullBucketDepth = bucketRef.getLocalDepth();
			
			String nextBucketIndex = "1"+previousFullBucketIndex.substring(previousFullBucketIndex.length()-previousFullBucketDepth);
			
			//Após inserir um novo bucket, o bucket de origem deve ter sua profundidade incrementada
			bucketRef.incrementLocalDepth();
			//Adiciono o arquivo do bucket nas referências e ativo ele
			addBucket(formatarStringBinaria(nextBucketIndex), bucketRef.getLocalDepth());
			
			//listas auxiliares que serão usadas para redistribuir elementos do bucket com o bucket novo.
			ArrayList<Registro> remainRegisters = new ArrayList<Registro>();
			ArrayList<Registro> moveRegisters   = new ArrayList<Registro>();
			
			//Redistribui os registros do bucket antigo.
			for(Registro rg : bucket.getRegistros())
			{
				//Faço o hash novamente para cada registro do bucket antigo, para identificar se ele fica no bucket em que está ou se vai para o novo.
				indexAux = hash(rg.getConteudo(), bucketRef.getLocalDepth());
				
				//Se o índice do elemento atual não for igual ao índice do novo elemento (que precisou do novo bucket), ele fica no bucket que está
				if(indexAux.equals(previousIndex))
					remainRegisters.add(rg);
				else
					moveRegisters.add(rg);
			}
			//Enquanto tenho a referencia do bucket cheio, atualizo os registros REHASHEDS dele
			if(hash(register.getConteudo(), bucketRef.getLocalDepth()).equals(previousIndex))
				remainRegisters.add(register);
			else
				moveRegisters.add(register); //Podem estar 4 nesse momento, caso, na duplicação, todos os registraos tenham vindo para esse bucket novo
			
			
			if(remainRegisters.size() < 3)
				bucket.setRegistros(remainRegisters);
			else
			{
				//A função está horrível. preciso refatorar.
				//Caso ela entre aqui, significa dizer que o antigo continua cheio.
				duplicarBuckets(bucket, bucketRef, formatarStringBinaria(nextBucketIndex), register, remainRegisters);
//				inlcuirRegistro(register);
				return;
			}
			
			//Revalido se a quantidade de registros está permitida para o bucket
			if(moveRegisters.size() < 3)
			{
				//Procuro a referencia do bucket para o novo índice
				bucketRef = buscarReferencia(formatarStringBinaria(nextBucketIndex));
				
				//Trago o novo bucket para a memória
				bucket = new Bucket(bucketRef.getPath());
				
				//Realizo a escrita da linha no arquivo
				bucket.setRegistros(moveRegisters);
				retorno = "INC:"+register.getConteudo()+"/"+this.globalDepth+","+bucketRef.getLocalDepth()+"\n";
			}
			else
			{
				duplicarBuckets(bucket, bucketRef, formatarStringBinaria(nextBucketIndex), register, moveRegisters);
//				retorno = "Foi necessário duplicar os buckets mais de uma vez.";
//				retorno = "DUP_DIR:";
				System.out.println(retorno);
			}
		}
		//Grava o out.txt
		gravarOutput(retorno);
	}
	
	private void duplicarBuckets(Bucket bucket, BucketReference bucketRef, String index, Registro register, ArrayList<Registro> regMove) throws IOException
	{
		String previousIndex;
		String indexAux;
		String retorno;
		//Se o bucket atual está cheio e sua profundidade é igual a do diretório, não posso duplica-lo sem ter sua referencia, então duplico o diretório.
		if(bucketRef.getLocalDepth() == this.globalDepth)
		{
			duplicarDiretorio();
			//Atualiza o hash com os digitos significativos para a nova profundidade
			previousIndex = index;//index anterior a atualização da profundidade global

			//index anterior a atualização, sendo atualizado com o global.
			index = hash(register.getConteudo());
		}
		else
			previousIndex = index;

		//Esse cara é o índice do bucket que está cheio, precisando se dividir.
		String previousFullBucketIndex = index;

		//Esse cara é o número de bits que devem ser levados em consideração no indice...
		int previousFullBucketDepth = bucketRef.getLocalDepth();

		String nextBucketIndex = "1"+previousFullBucketIndex.substring(previousFullBucketIndex.length()-previousFullBucketDepth);

		//Após inserir um novo bucket, o bucket de origem deve ter sua profundidade incrementada
		buscarReferencia(index).incrementLocalDepth();
		//Adiciono o arquivo do bucket nas referências e ativo ele
		addBucket(formatarStringBinaria(nextBucketIndex), buscarReferencia(index).getLocalDepth());
		
		
		//listas auxiliares que serão usadas para redistribuir elementos do bucket com o bucket novo.
		ArrayList<Registro> remainRegisters = new ArrayList<Registro>();
		ArrayList<Registro> moveRegisters   = new ArrayList<Registro>();
		
		//Redistribui os registros do bucket antigo.
		for(Registro rg : regMove)
		{
			//Faço o hash novamente para cada registro do bucket antigo, para identificar se ele fica no bucket em que está ou se vai para o novo.
			indexAux = hash(rg.getConteudo(), buscarReferencia(index).getLocalDepth());

			//Se o índice do elemento atual não for igual ao índice do novo elemento (que precisou do novo bucket), ele fica no bucket que está
			if(indexAux.equals(previousIndex))
				remainRegisters.add(rg);
			else
				moveRegisters.add(rg);
		}
		
		
		if(remainRegisters.size() < 4)
		{
			bucket = new Bucket(buscarReferencia(index).getPath());
			bucket.setRegistros(remainRegisters);
		}
		else
		{
			//A função está horrível. preciso refatorar.
			//Caso ela entre aqui, significa dizer que o antigo continua cheio.
			duplicarBuckets(bucket, bucketRef, formatarStringBinaria(nextBucketIndex), register, remainRegisters);
			//				inlcuirRegistro(register);
			return;
		}
		
		//Procuro a referencia do bucket para o novo índice
		bucketRef = buscarReferencia(formatarStringBinaria(nextBucketIndex));

		//Trago o novo bucket para a memória
		bucket = new Bucket(bucketRef.getPath());

		//Revalido se a quantidade de registros está permitida para o bucket
		if(moveRegisters.size() < 4)
		{
			//Realizo a escrita da linha no arquivo
			bucket.setRegistros(moveRegisters);
			retorno = "INC:"+register.getConteudo()+"/"+this.globalDepth+","+bucketRef.getLocalDepth()+"\n";
			retorno += "DUP_DIR:"+this.globalDepth+","+bucketRef.getLocalDepth()+"\n";
			gravarOutput(retorno);
		}
		else
		{
			duplicarBuckets(bucket, bucketRef, formatarStringBinaria(nextBucketIndex), register, moveRegisters);
			retorno = "Erro na inclusão. Seria necessário duplicar os buckets mais uma vez.";
			System.out.println(retorno);
		}
	}
	
	//Aplica o hash e retorna a String contendo os últimos digitos mais significativos baseado na profundidade
	public String hash(String key)
	{
		//Retorna o binário formatado com 4 digitos.
		return df.format(Integer.parseInt(Integer.toBinaryString(Integer.parseInt(key) & (1 << globalDepth) - 1)));
	}
	
	//Aplica o hash e retorna a String contendo os últimos digitos mais significativos baseado na profundidade
	public String hash(String key, int profundidade)
	{
		//Retorna o binário formatado com 4 digitos.
		return df.format(Integer.parseInt(Integer.toBinaryString(Integer.parseInt(key) & (1 << profundidade) - 1)));
	}
	
	//Função responsável por duplicar o diretório, dobrando suas referências e aumentando a profundidade global.
	private void duplicarDiretorio()
	{
		String indexAux = "";
		int qttBuckets  = this.bucketReferences.size();
		
		//Vai da quantidade atual de buckets até o dobro dessa quantidade.
		for(int i = qttBuckets; i < 2*qttBuckets; i++)
		{
			indexAux = formatarStringBinaria(i);
			String path = basePath +"/" + formatarStringBinaria(i%4) + ".txt";
			//Teoricamente, duplico a quantidade de referencias apontando para os mesmos buckets correspondentes
			//Duplico as referencias com profundidade local mantida. Será atualizada apenas na duplicação de bucket.
			this.bucketReferences.add(new BucketReference(path, globalDepth, indexAux, false));
		}
		this.globalDepth = this.globalDepth+1;
		//df = new DecimalFormat(String.join("", Collections.nCopies(this.globalDepth, "0"))); //Acho que deixar o índice com quantidade fixa tira a necessidade disso...
	}
	
	//Busca a referência previamente estabelecida com a duplicação de diretório e a ativa, aumentando a profundidade local e setando como ativo.
	private void addBucket(String indexAux, int depth) throws IOException
	{
		BucketReference br = buscarReferencia(indexAux);//transforma binário em decimal
		br.ativar(depth);
	}
	
	//Busca nas referencias do diretório por uma que atenda ao indice alvo e retorna a referencia.
	public BucketReference buscarReferencia(String indiceAlvo)
	{
		for(BucketReference br : this.bucketReferences)
		{
			if(br.getIndice().equals(indiceAlvo))
				return br;
		}
		return new BucketReference("", 0, "", false);
	}
	
	//Procura a h(chaveAlvo) nas referencias. Se tiver mais de uma entrada, todas serão selecionadas.
	public void buscaIgualdade(String chaveAlvo) throws IOException
	{
		String index = hash(chaveAlvo);
		String retorno = "";
		int tuplasSelecionadas = 0;
		int i = 0;
		BucketReference br;
		
		do
		{
			index = hash(chaveAlvo, globalDepth-i);
			br = buscarReferencia(index);
			i++;
		} while(!br.isAtivo());
		
		Bucket bucket = new Bucket(br.getPath());
		
		for(Registro register : bucket.getRegistros())
		{
			if(register.getConteudo().equals(chaveAlvo))
				tuplasSelecionadas++;
		}
		retorno += "BUS:" + chaveAlvo + "/" + tuplasSelecionadas;
		gravarOutput(retorno);
	}
	
	//Procura a h(chaveAlvo) nas referencias e a remove do bucket. Se tiver mais de uma entrada, todas serão removidas.
	public void rmRegistro(String chaveAlvo) throws IOException
	{
		String index = hash(chaveAlvo);
		String retorno = "";
		BucketReference br;
		int tuplasSelecionadas = 0;
		int i = 0;
		
		do
		{
			index = hash(chaveAlvo, globalDepth-i);
			br = buscarReferencia(index);
			i++;
		} while(!br.isAtivo());
		
		Bucket bucket = new Bucket(br.getPath());
		ArrayList<Registro> registros = new ArrayList<Registro>();
		
		for(Registro register : bucket.getRegistros())
		{
			if(!register.getConteudo().equals(chaveAlvo))
				registros.add(register);
			else
				tuplasSelecionadas++;
		}
		bucket.setRegistros(registros);
		retorno += "REM:" + chaveAlvo + "/" + tuplasSelecionadas +","+ this.globalDepth +","+ br.getLocalDepth();
		gravarOutput(retorno);
	}
	
	//Grava o argumento no arquivo out.txt
	public void gravarOutput(String conteudo) throws IOException
	{
		FileWriter arquivo = new FileWriter("C:/SGBD/inout/out.txt", true);
		PrintWriter escritor = new PrintWriter(arquivo, true);
		
		escritor.write(conteudo+"\n");
		
		arquivo.close();
	}
	
	//Retorna a conversão de um valor inteiro em uma string binária com 4 caractéres.
	public String formatarStringBinaria(int valor)
	{
		return df.format(Integer.parseInt(Integer.toBinaryString(valor)));
	}
	//Retorna a conversão de um valor inteiro em uma string binária com 4 caractéres.
	public String formatarStringBinaria(String valor)
	{
		return df.format(Integer.parseInt(valor));
	}
}