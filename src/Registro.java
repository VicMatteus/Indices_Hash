public class Registro
{
	private String posicao;
	private String conteudo;
	
	public Registro(String posicao, String conteudo)
	{
		this.posicao = posicao;
		this.conteudo = conteudo;
	}
	public String getPosicao()
	{
		return posicao;
	}
	public void setPosicao(String posicao)
	{
		this.posicao = posicao;
	}
	public String getConteudo()
	{
		return conteudo;
	}
	public void setConteudo(String conteudo)
	{
		this.conteudo = conteudo;
	}
	
	@Override
	public String toString()
	{
		return this.posicao + "," + this.conteudo;
	}
}
