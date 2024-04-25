import java.text.DecimalFormat;
import java.util.ArrayList;

public class Diretorio
{
	//A profundidade do diretório representa a quantidade de bits menos sig. que serão avaliados como índice.
	protected String basePath = "C:/SGBD";
	protected int globalDepth = 3;
	protected ArrayList<BucketReference> bucketReferences = new ArrayList<BucketReference>();
	
	public Diretorio(int globalDepth)
	{
		DecimalFormat df = new DecimalFormat("000");
		this.globalDepth = globalDepth;
		
		//Inicializo o diretório com 4 referencias de buckets de 00 a 11.
		//O nome path de cada bucket será o basePath+indice.
		for(int i=0; i<4; i++)
		{
			bucketReferences.add(new BucketReference(basePath, globalDepth, df.format(Integer.parseInt(Integer.toBinaryString(i)))));
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
	
	public void inlcuirRegistro(String key)
	{
		String lsd = hash(key);
		//esse valor vai me indicar qual referencia acessar
//		bucketReferences.indexOf();
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
	public void setBucketReferences(ArrayList<BucketReference> bucketReferences)
	{
		this.bucketReferences = bucketReferences;
	}
	
}
