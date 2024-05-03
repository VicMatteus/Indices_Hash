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
	//Diretório base onde criará a pasta com arquivos de indices, input e output.
	protected String basePath = "C:/SGBD";
	//O diretório possui uma lista de referencias à buckets.
	protected ArrayList<BucketReference> bucketReferences = new ArrayList<BucketReference>();
	protected int globalDepth = 2;
	//Um formatador para padronizar a quantidade de caracteres em 4.
	private DecimalFormat df = new DecimalFormat("0000");
	//Arranjo técnico para inversão de DUP_DIR com INC
	private String duplicaDirMsg = "";
	
	//Cria 4 referências partindo de 00 a 11 e as adiciona no atributo.
	public Diretorio(int globalDepth)
	{
		this.globalDepth = globalDepth;
		String indexAux;

		//O path de cada bucket será o basePath+indice.
		for(int i=0; i<2*this.globalDepth; i++)
		{
			indexAux = formatarStringBinaria(i);
			bucketReferences.add(new BucketReference(basePath, globalDepth, indexAux, true));
		}
	}
	
	//Getters padrão
	public ArrayList<BucketReference> getBucketReferences()
	{
		return bucketReferences;
	}
	public int getGlobalDepth() { return this.globalDepth; }
	public String getDuplicaDirMsg()
	{
		return duplicaDirMsg;
	}
	
	/*Funções auxiliares específicas*/
	
	//Aplica o hash e retorna a String contendo os últimos digitos mais significativos baseado na profundidade
	//Retorna o binário formatado com 4 digitos.
	public String hash(String key)
	{
		return df.format(Integer.parseInt(Integer.toBinaryString(Integer.parseInt(key) & (1 << globalDepth) - 1)));
	}
	
	//Aplica o hash e retorna a String contendo os últimos digitos mais significativos baseado na profundidade
	public String hash(String key, int profundidade)
	{
		return df.format(Integer.parseInt(Integer.toBinaryString(Integer.parseInt(key) & (1 << profundidade) - 1)));
	}
	
	//Busca a referência previamente estabelecida com a duplicação de diretório e a ativa, aumentando a profundidade local e setando como ativo.
	private void addBucket(String indexAux, int depth) throws IOException
	{
		BucketReference br = buscarReferencia(indexAux);//transforma binário em decimal
		br.ativar(depth);
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
	//Retorna a conversão de uma string em uma string binária com 4 caractéres.
	public String formatarStringBinaria(String valor)
	{
		return df.format(Integer.parseInt(valor));
	}
	
	//Função retorna a profundidade loca de uma chave de busca. Faz o hash e vai da profundidade global até a menor ativa disponível. retorna a menor.
	public String buscarLocalDepthDeChave(String chaveAlvo)
	{
		String index = hash(chaveAlvo);
		String retorno = "";
		int i = 0;
		BucketReference br;
		
		do
		{
			index = hash(chaveAlvo, globalDepth-i);
			br = buscarReferencia(index);
			i++;
		} while(!br.isAtivo());
		return Integer.toString(br.getLocalDepth());
	}
	
	//Gambi para alternar a ordem da mensagem de inclusão e duplicação de diretório
	public String duplicouDirMsg()
	{
		String retorno = this.duplicaDirMsg;
		
		this.duplicaDirMsg = "";
		
		return retorno;
	}
	
	
	/*
	* Recebe um objeto registro e faz a inclusão no bucket.
	* - baseado na chave do registro, faz o hash para a chave global(maior) e depois vai diminuindo para ver se encontra um bucket menor ativo
	* - após achar o bucket, instancia ele em memo
	* - se o bucket tiver espaço, a inclusão é direta.
	* - se não tiver espaço
	*   ~ verifica a necessidade de duplicar o diretório, isto é, se a p.local já estiver igual a p.global
	* - monta a string contendo o próximo indice de bucket reference que será referenciado e o ativa
	* - incrementa a pl do bucket atual
	* - varre o bucket atual passando o hash com o indice novo e separando que fica no bucket atual e quem vai para a próxima referencia
	* - seta os registros que ficam no bucekt atual e seta os que vão para o próximo bucket
	* - se os registros que ficam ou que vão forem mais do que 3, duplica os buckets.
	* */
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
//			retorno = "INC:"+register.getConteudo()+"/"+this.globalDepth+","+bucketRef.getLocalDepth();
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
				//Caso ela entre aqui, significa que o antigo continua cheio.
				duplicarBuckets(bucket, bucketRef, formatarStringBinaria(nextBucketIndex), register, remainRegisters);
				//Retorna, pois todos os registros foram rearranjados na função acima.
				return;
			}
			
			//Revalido se a quantidade de registros está permitida para o bucket novo
			if(moveRegisters.size() < 3)
			{
				//Procuro a referencia do bucket para o novo índice
				bucketRef = buscarReferencia(formatarStringBinaria(nextBucketIndex));
				
				//Trago o novo bucket para a memória
				bucket = new Bucket(bucketRef.getPath());
				
				//Realizo a escrita da linha no arquivo
				bucket.setRegistros(moveRegisters);
			}
			else
			{
				duplicarBuckets(bucket, bucketRef, formatarStringBinaria(nextBucketIndex), register, moveRegisters);
			}
		}
	}
	
	
	/* ex: 0001 -> 0101 -> 1101
	* index é o indice do bucket atualmente cheio (0101)
	* bucketRef é a referencia do bucket anterior ao bucket cheio (0001)
	* register é o registro que causou o overflow
	* regMove são os registros que estão no bucket cheio (0101)
	* */
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
		String previousFullBucketIndex = previousIndex;

		//Esse cara é o número de bits que devem ser levados em consideração no indice...
		int previousFullBucketDepth = bucketRef.getLocalDepth();

		//monta o próximo indice ao adicionar um bit "1" na frente do índice anterior -> 0101 -> 1101
		String nextBucketIndex = "1"+previousFullBucketIndex.substring(previousFullBucketIndex.length()-previousFullBucketDepth);

		//Após inserir um novo bucket, o bucket de origem deve ter sua profundidade incrementada
		buscarReferencia(previousIndex).incrementLocalDepth();
		//Adiciono o arquivo do bucket nas referências e ativo ele
		addBucket(formatarStringBinaria(nextBucketIndex), buscarReferencia(previousIndex).getLocalDepth());
		
		
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
		
		//Se os regs. não passarem do limite, seta direto
		if(remainRegisters.size() < 4)
		{
			bucket = new Bucket(buscarReferencia(index).getPath());
			bucket.setRegistros(remainRegisters);
		}
		else
		{
			//Caso ela entre aqui, significa dizer que o antigo continua cheio.
			duplicarBuckets(bucket, bucketRef, formatarStringBinaria(nextBucketIndex), register, remainRegisters);
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
			this.duplicaDirMsg = "DUP_DIR:"+this.globalDepth+","+bucketRef.getLocalDepth();
		}
		else
		{
			duplicarBuckets(bucket, bucketRef, formatarStringBinaria(nextBucketIndex), register, moveRegisters);
			System.out.println("Erro na inclusão. Seria necessário duplicar os buckets mais uma vez.");
		}
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
			//duplico a quantidade de referencias apontando para os mesmos buckets correspondentes
			//Duplico as referencias com profundidade local mantida. Será atualizada apenas na duplicação de bucket.
			this.bucketReferences.add(new BucketReference(path, globalDepth, indexAux, false));
		}
		this.globalDepth = this.globalDepth+1;
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
		String indexAux;
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
		
		/*
		* Tratar caso a exlcusão, deixe um bucket vazio
		* caso o bucket esteja vazio, size()==0
		*   bucket.ativo = false
		*   bucket.path = index anterior //precisa calcular index anterior
		*   bucket com index anterior.localDepth = -1
		*   se bucket nenhum tiver localDepth > bucket com index anterior.localDepth = -1
		*       globalDepth = -1
		*   realizar rehash no bucket com index anterior com a nova localDepth
		* */
		
		if(bucket.getRegistros().size() == 0 && br.getLocalDepth() > 2)
		{
			Boolean reduzGlobalDepth = true;
			String indexAnterior = df.format(Integer.parseInt(br.getIndice().substring(br.getIndice().length() - br.getLocalDepth()+1)));
			br.desativar(indexAnterior);//passo o indice para o qual o bucket desativado passará a apontar
			
			br = buscarReferencia(indexAnterior);
			br.setLocalDepth(br.getLocalDepth()-1);
			
			for(BucketReference brAux : this.bucketReferences)
			{
				if(brAux.getLocalDepth() >= this.globalDepth)
				{
					reduzGlobalDepth = false;
					break;
				}
			}
			if(reduzGlobalDepth)
			{
				this.globalDepth = globalDepth-1;
			}
			
			//rehash do bucket anterior
			bucket = new Bucket(br.getPath());
			ArrayList<Registro> remainRegisters = new ArrayList<Registro>();
			ArrayList<Registro> moveRegisters   = new ArrayList<Registro>();
			
			//Redistribui os registros do bucket antigo.
			for(Registro rg : bucket.getRegistros())
			{
				//Faço o hash novamente para cada registro do bucket atual para identificar se ele fica no bucket em que está ou se vai para o anterior
				indexAux = hash(rg.getConteudo(), br.getLocalDepth());
				
				//Se o índice do elemento atual for igual ao índice do bucket atual, ele fica no bucket que está
				if(indexAux.equals(indexAnterior))
					remainRegisters.add(rg);
				else
					moveRegisters.add(rg);
			}
			//seto os registros na br que estou no momento, verifico se é maior que zero, se não for, chamo a função de novo
			if(remainRegisters.size() != 0)
			{
				bucket.setRegistros(remainRegisters);
			}
			else
			{
				System.out.println("Necessário remover mais um nível de diretório");
			}
			
			indexAnterior = df.format(Integer.parseInt(br.getIndice().substring(br.getIndice().length() - br.getLocalDepth()+1)));
			bucket = new Bucket(buscarReferencia(indexAnterior).getPath());
			for(Registro rg : bucket.getRegistros())
			{
				moveRegisters.add(rg);
			}
			if(moveRegisters.size() < 4)
				bucket.setRegistros(moveRegisters);
			else
				System.out.println("Seria necessário duplicar o bucket: " + indexAnterior);
			
		}
		retorno += "REM:" + chaveAlvo + "/" + tuplasSelecionadas +","+ this.globalDepth +","+ br.getLocalDepth();
		gravarOutput(retorno);
	}
}