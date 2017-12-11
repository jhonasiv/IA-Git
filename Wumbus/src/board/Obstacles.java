
package board;

import java.awt.Point;
import java.util.ArrayList;

public class Obstacles
{
	
	// /TODO: Decidir se vai manter o minecart como um carrinho que segue os
	// trilhos, ou teletransportando para um destino.
	public enum Obstacle {
		ENTRADA, SAIDA, POCO, MONSTRO, OURO, ROCHA, ATALHO, TORRE, DESMORONAMENTO, BAU, FOGO
	};
	
	// / Pode escolher usar a flecha com fogo depois de chegar num numero de
	// rodadas muito grande ou depois de estar numa situacÃ£o dificil (cercado
	// por pocos por exemplo).
	// Pode talvez pegar o fogo como tocha e ganhar uma visao de um quadrante a
	// mais.
	
	private static int maxPocos;
	private static int maxRochas;
	private static int maxAtalhos;
	private static int maxTorres;
	private static int maxBaus;
	private static int maxFogos;
	private static int maxDesm;
	
	private Point humano = new Point();
	private Point entrada = new Point();
	private Point saida = new Point();
	private ArrayList<Point> pocos = new ArrayList<Point>();
	private Point monstro = new Point();
	private Point ouro = new Point();
	private ArrayList<Point> rochas = new ArrayList<Point>();
	private ArrayList<Point> atalhos = new ArrayList<Point>();
	private ArrayList<Point> torres = new ArrayList<Point>();
	private ArrayList<Point> desm = new ArrayList<Point>();
	private ArrayList<Point> fogos = new ArrayList<Point>();
	private ArrayList<Point> baus = new ArrayList<Point>();
	
	private Obstacle obs = Obstacle.ENTRADA;
	
	private int spacesLeft = 0;
	private int leftWall = 0, rightWall, upperWall, lowerWall = 0;
	private int numPocos = 0;
	private int numRochas = 0;
	private int numAtalhos = 0;
	private int numTorres = 0;
	private int numDesm = 0;
	private int numFogos = 0;
	private int numBaus = 0;
	
	private boolean totalmenteDefinida = false;
	
	private boolean entradaDefinida = false;
	private boolean saidaDefinida = false;
	private boolean pocosDefinidos = false;
	private boolean monstroDefinido = false;
	private boolean ouroDefinido = false;
	private boolean atalhosDefinidos = false;
	private boolean torresDefinidas = false;
	private boolean rochaDefinida = false;
	private boolean desmoronamentosDefinidos = false;
	private boolean bausDefinidos = false;
	private boolean fogosDefinidos = false;
	
	private int numTentativas = 0;
	
	public boolean doneDefining()
	{
		return totalmenteDefinida;
	}
	
	public void getSize(int width, int height)
	{
		rightWall = width - 1;
		upperWall = height - 1;
		spacesLeft = width * height;
		maxPocos = width * height / 18;
		maxRochas = width * height / 5;
		maxAtalhos = 2 * (int) (width * height / 100); /// Multiplicado por 2 para forçar a ser par
		maxTorres = width * height / 100;
		maxDesm = width * height / 40;
		maxBaus = width * height / 100;
		maxFogos = width * height / 10;
	}
	
	public Point getHuman()
	{
		return humano;
	}
	
	public Point getEntrada()
	{
		return entrada;
	}
	
	public Point getSaida()
	{
		return saida;
	}
	
	public ArrayList<Point> getPocos()
	{
		return pocos;
	}
	
	public Point getMonstro()
	{
		return monstro;
	}
	
	public Point getOuro()
	{
		return ouro;
	}
	
	public ArrayList<Point> getTorres()
	{
		return torres;
	}
	
	public ArrayList<Point> getFogos()
	{
		return fogos;
	}
	
	public ArrayList<Point> getAtalhos()
	{
		return atalhos;
	}
	
	public String enforceRules(int linha, int coluna, String atual)
	{
		String result = "";
		switch (obs)
		{
			case ENTRADA:
				
				float maxQuadrantes = Math.abs(2 * (rightWall + 1) + 2 * (upperWall + 1) - 4);
				
				if(!entradaDefinida)
				{
					if(linha == lowerWall || linha == upperWall || coluna == leftWall || coluna == rightWall)
					{
						if(Math.random() <= (1 / maxQuadrantes))
						{
							spacesLeft -= 1;
							result = "E";
							entrada.setLocation(linha, coluna);
							humano.setLocation(linha, coluna);
							entradaDefinida = true;
							break;
						}
					}
					
					if(linha == upperWall && coluna == rightWall)
					{
						numTentativas++;
						if(numTentativas == 2)
						{
							spacesLeft -= 1;
							result = "E";
							entrada.setLocation(linha, coluna);
							humano.setLocation(linha, coluna);
							entradaDefinida = true;
							numTentativas = 0;
						}
					}
				}
				break;
			case SAIDA:
				
				if(!saidaDefinida)
				{
					int possibleExit = 0;
					boolean horizontal = false;
					
					if(entrada.x == lowerWall)
					{
						horizontal = true;
						possibleExit = upperWall;
					}
					else if(entrada.x == upperWall)
					{
						horizontal = true;
						possibleExit = lowerWall;
					}
					else if(entrada.y == leftWall)
					{
						horizontal = false;
						possibleExit = rightWall;
					}
					else if(entrada.y == rightWall)
					{
						horizontal = false;
						possibleExit = leftWall;
					}
					
					// System.out.println((float)1/(upperWall+1));
					if(!horizontal)
					{
						if(coluna == possibleExit)
						{
							if(Math.random() <= (float) 1 / (rightWall + 1))
							{
								spacesLeft -= 1;
								saida.setLocation(linha, coluna);
								result = "S";
								saidaDefinida = true;
							}
						}
					}
					else
					{
						if(linha == possibleExit)
						{
							if(Math.random() <= (float) 1 / (upperWall + 1))
							{
								spacesLeft -= 1;
								saida.setLocation(linha, coluna);
								result = "S";
								saidaDefinida = true;
							}
						}
					}
				}
				break;
			case POCO:
				if(!pocosDefinidos)
				{
					// System.out.println(linha + ", " + coluna);
					if(numPocos < maxPocos)
					{
						if((Math.abs(linha - entrada.x) + Math.abs(coluna - entrada.y)) >= 3 && (Math.abs(linha - saida.x) + Math.abs(coluna - saida.y)) >= 3)
						{
							if(Math.random() < (float) maxPocos / (spacesLeft))
							{
								spacesLeft -= 1;
								pocos.add(new Point(linha, coluna));
								result = "P";
								numPocos++;
							}
							if(numPocos == maxPocos)
							{
								System.out.println("POÇOS = " + numPocos);
								pocosDefinidos = true;
							}
						}
					}
				}
				if(linha == upperWall && coluna == rightWall)
				{
					System.out.println("POÇOS = " + numPocos);
					pocosDefinidos = true;
				}
				break;
			case MONSTRO:
				if(!monstroDefinido)
				{
					if((Math.abs(linha - entrada.x) + Math.abs(coluna - entrada.y)) >= 5 && (Math.abs(linha - saida.x) + Math.abs(coluna - saida.y)) >= 2)
					{
						for (int n = 0; n < atual.length(); n++)
						{
							if(atual.charAt(n) != 'P')
							{
								// System.out.println("asndanda");
								if(Math.random() < (float) 1 / (spacesLeft - 43))
								{
									spacesLeft -= 1;
									result = "M";
									monstro.setLocation(linha, coluna);
									monstroDefinido = true;
								}
							}
						}
					}
				}
				break;
			case OURO:
				if(!ouroDefinido)
				{
					if(Math.abs(linha - monstro.x) + Math.abs(coluna - monstro.y) <= 4 && Math.abs(linha - monstro.x) + Math.abs(coluna - monstro.y) != 0)
					{
						for (int n = 0; n < atual.length(); n++)
						{
							if(atual.charAt(n) != 'P')
							{
								if(Math.random() < (float) 1 / 12)
								{
									spacesLeft -= 1;
									result = "O";
									ouro.setLocation(linha, coluna);
									ouroDefinido = true;
								}
							}
						}
					}
				}
				break;
			case ROCHA:
				if(!rochaDefinida)
				{
					if((Math.abs(linha - entrada.x) + Math.abs(coluna - entrada.y)) >= 2 && (Math.abs(linha - saida.x) + Math.abs(coluna - saida.y)) >= 2)
					{
						for (int n = 0; n < atual.length(); n++)
						{
							if(atual.charAt(n) != 'P' && atual.charAt(n) != 'M' && atual.charAt(n) != 'O')
							{
								if(Math.random() < (float) 1 / (spacesLeft - 10))
								{
									spacesLeft -= 1;
									result = "R";
									rochas.add(new Point(linha, coluna));
									numRochas++;
								}
								if(numRochas == maxRochas)
								{
									System.out.println("ROCHAS = " + numRochas);
									rochaDefinida = true;
								}
							}
						}
					}
					if(linha == upperWall && coluna == rightWall)
					{
						numTentativas++;
						if(numTentativas == 50)
						{
							System.out.println("ROCHAS = " + numRochas);
							rochaDefinida = true;
							numTentativas = 0;
						}
					}
				}
				break;
			case ATALHO:
				if(!atalhosDefinidos)
				{
					if((Math.abs(linha - entrada.x) + Math.abs(coluna - entrada.y)) >= 4 && (Math.abs(linha - saida.x) + Math.abs(coluna - saida.y)) >= 4)
					{
						for (int n = 0; n < atual.length(); n++)
						{
							if(atual.charAt(n) != 'P' && atual.charAt(n) != 'M' && atual.charAt(n) != 'R' && atual.charAt(n) != 'O')
							{
								if(numAtalhos == 0)
								{
									if(Math.random() < (float) 1 / (spacesLeft - 56))
									{
										spacesLeft -= 1;
										result = "A";
										atalhos.add(new Point(linha, coluna));
										numAtalhos++;
									}
								}
								else
								{
									boolean farEnough = false;
									int numItt = 0;
									for (int x = 0; x < atalhos.size(); x++)
									{
										if(Math.abs(linha - atalhos.get(atalhos.size() - 1).x) + Math.abs(coluna - atalhos.get(atalhos.size() - 1).y) <= 5)
										{
											if(Math.abs(linha - atalhos.get(x).x) + Math.abs(coluna - atalhos.get(x).y) > 2)
											{
												numItt++;
												if(numItt == atalhos.size())
												{
													farEnough = true;
												}
											}
											if(farEnough)
											{
												if(Math.random() < (float) 1 / 25)
												{
													spacesLeft -= 1;
													result = "A";
													atalhos.add(new Point(linha, coluna));
													numAtalhos++;
													break;
												}
											}
										}
									}
								}
								if(numAtalhos == maxAtalhos)
								{
									System.out.println("ATALHOS: " + numAtalhos + " - " + maxAtalhos);
									atalhosDefinidos = true;
								}
							}
						}
					}
				}
				break;
			case TORRE:
				if(!torresDefinidas)
				{
					if((Math.abs(linha - entrada.x) + Math.abs(coluna - entrada.y)) >= 6 && (Math.abs(linha - saida.x) + Math.abs(coluna - saida.y)) >= 4)
					{
						for (int n = 0; n < atual.length(); n++)
						{
							if(atual.charAt(n) != 'P' && atual.charAt(n) != 'M' && atual.charAt(n) != 'R' && atual.charAt(n) != 'A')
							{
								if(Math.random() < (float) 1 / (spacesLeft - 59))
								{
									spacesLeft -= 1;
									result = "T";
									torres.add(new Point(linha, coluna));
									numTorres++;
								}
								if(numTorres == maxTorres)
								{
									torresDefinidas = true;
								}
							}
						}
					}
					if(linha == upperWall && coluna == rightWall)
					{
						numTentativas++;
						if(numTentativas == 2)
						{
							torresDefinidas = true;
							numTentativas = 0;
						}
					}
				}
				break;
			case DESMORONAMENTO:
				if(!desmoronamentosDefinidos)
				{
					if((Math.abs(linha - entrada.x) + Math.abs(coluna - entrada.y)) >= 4 && (Math.abs(linha - saida.x) + Math.abs(coluna - saida.y)) >= 3)
					{
						for (int n = 0; n < atual.length(); n++)
						{
							if(atual.charAt(n) != 'P' && atual.charAt(n) != 'R' && atual.charAt(n) != 'T')
							{
								if(Math.random() < (float) 1 / (spacesLeft - 20))
								{
									spacesLeft -= 1;
									result = "D";
									desm.add(new Point(linha, coluna));
									numDesm++;
								}
								if(numDesm == maxDesm)
								{
									desmoronamentosDefinidos = true;
								}
							}
						}
					}
					if(linha == upperWall && coluna == rightWall)
					{
						numTentativas++;
						if(numTentativas == 2)
						{
							desmoronamentosDefinidos = true;
							numTentativas = 0;
						}
					}
				}
				break;
			case BAU:
				if(!bausDefinidos)
				{
					if((Math.abs(linha - entrada.x) + Math.abs(coluna - entrada.y)) >= 5 && (Math.abs(linha - saida.x) + Math.abs(coluna - saida.y)) >= 4)
					{
						for (int n = 0; n < atual.length(); n++)
						{
							if(atual.charAt(n) != 'P' && atual.charAt(n) != 'R')
							{
								if(Math.random() < (float) 1 / (spacesLeft - 81))
								{
									spacesLeft -= 1;
									result = "B";
									baus.add(new Point(linha, coluna));
									numBaus++;
								}
								if(numBaus == maxBaus)
								{
									bausDefinidos = true;
								}
							}
						}
					}
					if(linha == upperWall && coluna == rightWall)
					{
						numTentativas++;
						if(numTentativas == 2)
						{
							bausDefinidos = true;
							numTentativas = 0;
						}
					}
				}
				break;
			case FOGO:
				if(!fogosDefinidos)
				{
					if((Math.abs(linha - entrada.x) + Math.abs(coluna - entrada.y)) >= 6 && (Math.abs(linha - saida.x) + Math.abs(coluna - saida.y)) >= 3)
					{
						for (int n = 0; n < atual.length(); n++)
						{
							if(atual.charAt(n) != 'P' && atual.charAt(n) != 'R')
							{
								// System.out.println(numFogos);
								if(Math.random() < (float) 1 / (spacesLeft - 50))
								{
									spacesLeft -= 1;
									result = "F";
									fogos.add(new Point(linha, coluna));
									numFogos++;
								}
								if(numFogos == maxFogos)
								{
									fogosDefinidos = true;
								}
							}
						}
					}
					if(linha == upperWall && coluna == rightWall)
					{
						numTentativas++;
						if(numTentativas == 8)
						{
							fogosDefinidos = true;
							numTentativas = 0;
						}
					}
				}
				break;
		}
		
		if(entradaDefinida)
		{
			obs = Obstacle.SAIDA;
			if(saidaDefinida)
			{
				obs = Obstacle.POCO;
				if(pocosDefinidos)
				{
					obs = Obstacle.MONSTRO;
					if(monstroDefinido)
					{
						obs = Obstacle.OURO;
						if(ouroDefinido)
						{
							obs = Obstacle.ROCHA;
							if(rochaDefinida)
							{
								obs = Obstacle.ATALHO;
								if(atalhosDefinidos)
								{
									obs = Obstacle.TORRE;
									if(torresDefinidas)
									{
										obs = Obstacle.DESMORONAMENTO;
										if(desmoronamentosDefinidos)
										{
											obs = Obstacle.BAU;
											if(bausDefinidos)
											{
												obs = Obstacle.FOGO;
												if(fogosDefinidos)
												{
													totalmenteDefinida = true;
													
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
		
		return result;
	}
}
