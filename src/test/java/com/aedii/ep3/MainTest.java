package com.aedii.ep3;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class MainTest {
	
	@Test
	public void deve_Enderecar_Componentes_Conexas() {
		
		Grafo grafo = criarGrafo();
		
		List<Integer> resultado = Main.calcularComponentesConexas(grafo);
		
		List<Integer> deveriaSer = new ArrayList<>();
		deveriaSer.add(1);
		deveriaSer.add(1);
		deveriaSer.add(2);
		deveriaSer.add(1);
		deveriaSer.add(1);
		deveriaSer.add(3);
		
		assertEquals(6, resultado.size());
		
		for(int i = 0; i < resultado.size(); i++) {
			assertEquals(deveriaSer.get(i), resultado.get(i));
		}
	}
	
	@Test
	public void deve_Retornar_Lista_Com_Os_Tamanhos_Dos_Componentes_Conexos_De_Uma_Lista_De_Ids() {
		
		List<Integer> componentesConexas = new ArrayList<>();
		componentesConexas.add(1);
		componentesConexas.add(1);
		componentesConexas.add(2);
		componentesConexas.add(1);
		componentesConexas.add(1);
		componentesConexas.add(3);
		
		List<Integer> resultado = Main.pegarTamanhosDeComponentesConexas(componentesConexas);
		
		assertEquals(3, resultado.size());
		assertEquals(Integer.valueOf(4), resultado.get(0));
		assertEquals(Integer.valueOf(1), resultado.get(1));
		assertEquals(Integer.valueOf(1), resultado.get(2));
	}
	
	@Test
	public void deve_Calcular_Frequencia_De_Componentes_Conexas() {
		
		Grafo grafo = criarGrafo();
		
		Map<Integer, Integer> map = Main.calcularFrequenciaDeComponentesConexas(grafo);
		
		assertEquals(Integer.valueOf(1), map.get(4));
		assertEquals(Integer.valueOf(2), map.get(1));
		
	}
	
	@Test(expected = Test.None.class)
	public void deve_Gerar_Arquivo() throws IOException {
		
		Map<Integer, Integer> mapDeFrequencias = new HashMap<>();
		mapDeFrequencias.put(1, 2);
		mapDeFrequencias.put(3, 4);
		mapDeFrequencias.put(0, 517);
		
		Main.exportarMapDeFrequenciasParaTxt(mapDeFrequencias);
	}

	private Grafo criarGrafo() {
		
		Grafo grafo = new Grafo();
		
		List<String> zonasDestinoVertice1 = new ArrayList<>();
		zonasDestinoVertice1.add("1");
		zonasDestinoVertice1.add("2");
		
		List<String> zonasDestinoVertice2 = new ArrayList<>();
		zonasDestinoVertice2.add("1");
		zonasDestinoVertice2.add("3");
		
		List<String> zonasDestinoVertice3 = new ArrayList<>();
		zonasDestinoVertice3.add("5");
		
		List<String> zonasDestinoVertice4 = new ArrayList<>();
		zonasDestinoVertice4.add("2");
		
		List<String> zonasDestinoVertice5 = new ArrayList<>();
		zonasDestinoVertice5.add("3");
		zonasDestinoVertice5.add("6");
		
		List<String> zonasDestinoVertice6 = new ArrayList<>();
		zonasDestinoVertice4.add("100");
		zonasDestinoVertice4.add("517");
		
		Vertice vertice1 = new Vertice("1111", zonasDestinoVertice1);
		Vertice vertice2 = new Vertice("2222", zonasDestinoVertice2);
		Vertice vertice3 = new Vertice("3333", zonasDestinoVertice3);
		Vertice vertice4 = new Vertice("4444", zonasDestinoVertice4);
		Vertice vertice5 = new Vertice("5555", zonasDestinoVertice5);
		Vertice vertice6 = new Vertice("6666", zonasDestinoVertice6);
		
		vertice1.addAdj(vertice2);
		vertice1.addAdj(vertice4);
		
		vertice2.addAdj(vertice1);
		vertice2.addAdj(vertice5);
		
		vertice4.addAdj(vertice1);
		
		vertice5.addAdj(vertice2);
		
		List<Vertice> vertices = new ArrayList<>();
		vertices.add(vertice1);
		vertices.add(vertice2);
		vertices.add(vertice3);
		vertices.add(vertice4);
		vertices.add(vertice5);
		vertices.add(vertice6);
		
		grafo.setVertices(vertices);
		
		return grafo;
	}

}
