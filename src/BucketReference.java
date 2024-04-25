public class BucketReference
{
	protected String path;
	protected String indice;
	protected int localDepth = 3;
	
	public BucketReference(String path, int localDepth, String indice)
	{
		this.path = path + "/"+indice+".txt";
		this.localDepth = localDepth;
		this.indice = indice;
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
	public String getIndice()
	{
		return indice;
	}
	public void setIndice(String indice)
	{
		this.indice = indice;
	}
	
}
