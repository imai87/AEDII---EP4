package com.aedii.ep3;

import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.exceptions.CsvException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.*;

import org.apache.commons.lang3.StringUtils;

public class Main {

    private static final int NAO_VERIFICADA = -1;

    /**
     * Vamos montar o {@link Grafo} utilizando a estrutura de lista de adjacencia.
     * <p>
     * Os nos sao os entrevistados, representados pela classe {@link Vertice}.
     *
     * @param args
     * @throws IOException
     * @throws CsvException
     */
    public static void main(String[] args) throws IOException, CsvException {

        // Reader reader = Files.newBufferedReader(Paths.get("OD_2017.csv"));


        String filename = "src/main/resources/od_2017_v2.csv";

        Reader reader = Files.newBufferedReader(Paths.get(filename));

        List<Entrevistado> pessoasTotal = new CsvToBeanBuilder<Entrevistado>(reader).withType(Entrevistado.class)
                .build().parse();

//___________________________________________________________________________________________________________________________________
        // Lista de zonas que será passada para o Histograma
//        double[] valores = null;
//        List<Double> zonas = new ArrayList<>();
//
//        for (Entrevistados entrevistados : pessoasTotal) {
//            zonas.add(Double.parseDouble(entrevistados.getZonaDestino()));
//        }
//
//        valores = ArrayUtils.toPrimitive(zonas.toArray(new Double[zonas.size()]));

        // new Histograma(valores);
//____________________________________________________________________________________________________________________________________

        // Remove entrevistados com Zona de Destino igual a ZERO
        pessoasTotal.removeIf(entrevistado -> entrevistado.getZonaDestino().equals("0"));

//		printListaResultado(pessoasTotal);

        Grafo grafo = criarVertices(pessoasTotal);

//		printListaResultado(grafo.getVertices());

        adicionarAdjacencia(grafo);

        // Para imprimir depois que o grafo jah estiver com as vertices adjacentes
        // adicionados
//		printListaResultado(grafo.getVertices());

        System.out.println("Numero de arestas: " + grafo.getNumArestas());

        Map<Integer, Integer> mapDeFrequencias = calcularFrequenciaDeComponentesConexas(grafo);

        exportarMapDeFrequenciasParaTxt(mapDeFrequencias);
    }

    /**
     * Cria um {@link Grafo} e o popula com vertices. Vamos usar lista de adjacencia
     * para montar o grafo, porem, esse metodo nao adiciona os vertices adjacentes,
     * somente cria uma lista contendo todos os vertices, alem de juntar todos os
     * frequentadores duplicados que continham no {@link pessoasTotal}.
     * <p>
     * Se rodar o teste, tera uma visao melhor do que esta escrito acima.
     *
     * @param pessoasTotal Lista da entidade {@link Entrevistado} traduzida
     *                     diretamente do CSV.
     * @return um grafo contendo uma lista com todos os vertices.
     */
    private static Grafo criarVertices(List<Entrevistado> pessoasTotal) {

        Grafo grafo = new Grafo();

        int i = 0;
        while (i < pessoasTotal.size()) {

            String entrevistado = pessoasTotal.get(i).getFrequentador();
            List<String> coordenadas = new ArrayList<>();

            while (i < pessoasTotal.size()) {

                String proxEntrevistado = pessoasTotal.get(i).getFrequentador();

                if (entrevistado.equals(proxEntrevistado)) {

                    String proxZonaDestino = pessoasTotal.get(i).getZonaDestino();
                    coordenadas.add(proxZonaDestino);

                    i++;
                } else
                    break;
            }

            grafo.addVertice(entrevistado, coordenadas);
        }

        return grafo;
    }

    private static void adicionarAdjacencia(Grafo grafo) {

        int numArestas = 0;

        int numVertice = 0;

        for (Vertice vertice : grafo.getVertices()) {

            double grauDoVertice = 0;

            for (String zonaDestino : vertice.getZonaDestino()) {
                for (Vertice outroVertice : grafo.getVertices()) {

                    if (outroVertice.equals(vertice))
                        continue;

                    for (String outraZonaDestino : outroVertice.getZonaDestino()) {
                        if (outraZonaDestino.equals(zonaDestino)) {

                            vertice.addAdj(outroVertice);
                            numArestas++;
                            grauDoVertice++;
                        }
                    }
                }
            }

            vertice.setGrau(grauDoVertice);
            grafo.addGrausDosVertices(grauDoVertice);
            numVertice++;

            System.out
                    .println("Vertice " + StringUtils.leftPad(Integer.toString(numVertice), 5, "0") + " -> " + vertice);
        }

        grafo.setNumArestas(numArestas / 2);
    }

    private static <T> void printListaResultado(List<T> lista) {
        lista.forEach(System.out::println);
    }

    protected static Map<Integer, Integer> calcularFrequenciaDeComponentesConexas(Grafo grafo) {

        List<Integer> componentesConexas = calcularComponentesConexas(grafo);

        List<Integer> listaDeTamanhosDeComponentesConexas = pegarTamanhosDeComponentesConexas(componentesConexas);

        return agruparComponentesConexasPorTamanho(listaDeTamanhosDeComponentesConexas);
    }

    protected static void exportarMapDeFrequenciasParaTxt(Map<Integer, Integer> mapDeFrequencias) throws IOException {

        String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH-mm-ss"));

        String caminho = "./src/main/resources/resultadosEP4/Resultado " + time + ".txt";

        FileWriter arq = new FileWriter(caminho);
        PrintWriter gravarArq = new PrintWriter(arq);

        gravarArq.println("Tamanho da componente conexa -> Quantidade de ocorrências\n");

        for (Map.Entry<Integer, Integer> map : mapDeFrequencias.entrySet()) {
            gravarArq.println(map.getKey() + " -> " + map.getValue());
        }

        arq.close();
    }

    protected static List<Integer> pegarTamanhosDeComponentesConexas(List<Integer> componentesConexas) {

        Collections.sort(componentesConexas);

        List<Integer> listaDeTamanhosDeComponentesConexas = new ArrayList<>();

        for (int i = 0; i < componentesConexas.size(); ) {

            int id = componentesConexas.get(i);
            int tamanhoComponente = 0;

            int proximoId = i;
            while (proximoId < componentesConexas.size() && id == componentesConexas.get(proximoId)) {
                tamanhoComponente++;
                proximoId++;
                i++;
            }

            listaDeTamanhosDeComponentesConexas.add(tamanhoComponente);
        }

        return listaDeTamanhosDeComponentesConexas;
    }

    private static Map<Integer, Integer> agruparComponentesConexasPorTamanho(List<Integer> lista) {

        Map<Integer, Integer> mapFrequencia = new HashMap<>();

        for (int i = 0; i < lista.size(); i++) {

            if (mapFrequencia.containsKey(lista.get(i))) {
                mapFrequencia.put(lista.get(i), mapFrequencia.get(lista.get(i)) + 1);
            } else {
                mapFrequencia.put(lista.get(i), 1);
            }
        }

        return mapFrequencia;
    }

    /**
     * Endereca ids numa lista de inteiros que representa uma lista de
     * {@link Vertice}s. Ids iguais significam que estao numa mesma componente
     * conexa do grafo.
     *
     * @param grafo O grafo.
     * @return Uma lista com ids
     */
    protected static List<Integer> calcularComponentesConexas(Grafo grafo) {

        List<Integer> listaDeIds = new ArrayList<>();

        int id = 1;

        for (int i = 0; i < grafo.getVertices().size(); i++) {
            listaDeIds.add(NAO_VERIFICADA);
        }

        for (int i = 0; i < grafo.getVertices().size(); i++) {
            if (listaDeIds.get(i) == NAO_VERIFICADA) {
                enderecarVerticesAdjacentes(grafo.getVertices(), grafo.getVertices().get(i), listaDeIds, id++);
            }
        }

        return listaDeIds;
    }

    private static void geraHistograma() {

        String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH-mm-ss"));

        String caminho = "./src/main/resources/resultadosEP4/Resultado " + time + ".txt";

        FileWriter arq = new FileWriter(caminho);
        PrintWriter gravarArq = new PrintWriter(arq);

        gravarArq.println("Tamanho da componente conexa -> Quantidade de ocorrências\n");

        for (Map.Entry<Integer, Integer> map : mapDeFrequencias.entrySet()) {
            gravarArq.println(map.getKey() + " -> " + map.getValue());
        }

        arq.close();
    }

//		 -> Gera um arquivo utilizado para criar a base usada para
//		gerar o histograma solicitado. Ele também é responsável por chamar o método
}


    public static void buscaLargura() {


        List<Integer> listaDeIds = new ArrayList<>();

        int id = 1;

        for (int i = 0; i < grafo.getVertices().size(); i++) {
            listaDeIds.add(NAO_VERIFICADA);
        }

        for (int i = 0; i < grafo.getVertices().size(); i++) {
            if (listaDeIds.get(i) == NAO_VERIFICADA) {
                enderecarVerticesAdjacentes(grafo.getVertices(), grafo.getVertices().get(i), listaDeIds, id++);
            }
        }

        listaDeIds.set(vertices.indexOf(vertice), id);

        for (int i = 0; i < vertice.getAdj().size(); i++) {
            if (listaDeIds.get(vertices.indexOf(vertice.getAdj().get(i))) == NAO_VERIFICADA) {
                enderecarVerticesAdjacentes(vertices, vertice.getAdj().get(i), listaDeIds, id);
            }
        }

        return listaDeIds;

//		-> Este método recebe como parâmetro o nó que ele deve começar a
//		busca em largura para calcular as distâncias e o dicionário de par de vizinhos, para calcular a
//		distância do nó inicial até o atual da busca (source).
    }

    private static calculaDistanciaDosNos() {
        List<Integer> listaDeIds = new ArrayList<>();

        int id = 1;

        for (int i = 0; i < grafo.getVertices().size(); i++) {
            listaDeIds.add(NAO_VERIFICADA);
        }

        for (int i = 0; i < grafo.getVertices().size(); i++) {
            if (listaDeIds.get(i) == NAO_VERIFICADA) {
                enderecarVerticesAdjacentes(grafo.getVertices(), grafo.getVertices().get(i), listaDeIds, id++);
            }
        }

//		-> Chamado para cada vizinho dos nós da componente gigante,
//				calcula a distância entre o nó source e o vizinho corrente na busca em largura.
    }

    public static Void adicionaFrequencia() {

        int numArestas = 0;

        int numVertice = 0;

        for (Vertice vertice : grafo.getVertices()) {

            double grauDoVertice = 0;

            for (String zonaDestino : vertice.getZonaDestino()) {
                for (Vertice outroVertice : grafo.getVertices()) {

                    if (outroVertice.equals(vertice))
                        continue;

                    for (String outraZonaDestino : outroVertice.getZonaDestino()) {
                        if (outraZonaDestino.equals(zonaDestino)) {

                            vertice.addAdj(outroVertice);
                            numArestas++;
                            grauDoVertice++;
                        }
                    }
                }
            }

            vertice.setGrau(grauDoVertice);
            grafo.addGrausDosVertices(grauDoVertice);
            numVertice++;

            System.out
                    .println("Frequencia " + StringUtils.leftPad(Integer.toString(numVertice), 5, "0") + " -> " + vertice);
        }

        grafo.setNumArestas(numArestas / 2);

    }

    private static void enderecarVerticesAdjacentes(List<Vertice> vertices, Vertice vertice, List<Integer> listaDeIds,
                                                    int id) {

        listaDeIds.set(vertices.indexOf(vertice), id);

        for (int i = 0; i < vertice.getAdj().size(); i++) {
            if (listaDeIds.get(vertices.indexOf(vertice.getAdj().get(i))) == NAO_VERIFICADA) {
                enderecarVerticesAdjacentes(vertices, vertice.getAdj().get(i), listaDeIds, id);
            }
        }
    }
}
