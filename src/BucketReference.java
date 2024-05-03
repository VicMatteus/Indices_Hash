public class BucketReference
{
	protected String path;
	protected String indice;
	protected int localDepth = 3;
	private boolean ativo = false;
	
	/*
	* Armazena:
	* o caminho para o arquivo(ponteiro),
	* profundidade local,
	* indice de referencia,
	* se está ativo
	* */
	public BucketReference(String path, int localDepth, String indice, boolean ativo)
	{
		this.localDepth = localDepth;
		this.indice = indice;
		this.ativo = ativo;
		if(ativo)
			this.path = path + "/"+indice+".txt";
		else
			this.path = path;
	}
	
	public String getPath()
	{
		return path;
	}
	public void setPath(String path)
	{
		this.path = path;
	}
	public int getLocalDepth()
	{
		return localDepth;
	}
	public void setLocalDepth(int localDepth)
	{
		this.localDepth = localDepth;
	}
	public void incrementLocalDepth(){ this.localDepth = this.localDepth+1; }
	public String getIndice()
	{
		return indice;
	}
	public void setIndice(String indice)
	{
		this.indice = indice;
	}
	public boolean isAtivo()
	{
		return ativo;
	}
	
	
	public void ativar(int depth)
	{
		this.ativo = true;
		this.localDepth = depth;
		this.path = path.substring(0, 8) + this.indice+".txt";
	}
	
	public void desativar(String auxPath)
	{
		this.ativo = false;
		this.localDepth = localDepth-1;
		this.path = path.substring(0, 8) + auxPath +".txt";
	}
	
}
