package ks.constants;

import java.util.*;

public class L1DollItemId {
    public final static List<Integer> dollList1 = Arrays.asList(
            L1ItemId.DOLL_버그베어,
            L1ItemId.DOLL_목각,
            L1ItemId.DOLL_돌골렘,
            L1ItemId.DOLL_늑대인간,
            L1ItemId.DOLL_크러스트시안,
            L1ItemId.DOLL_에티
    );

    public final static List<Integer> dollList2 = Arrays.asList(
            L1ItemId.DOLL_서큐,
            L1ItemId.DOLL_장로,
            L1ItemId.DOLL_코카,
            L1ItemId.DOLL_눈사람,
            L1ItemId.DOLL_인어,
            L1ItemId.DOLL_라바골렘
    );

    public final static List<Integer> dollList3Normal = Arrays.asList(
            L1ItemId.DOLL_자이언트,
            L1ItemId.DOLL_흑장로,
            L1ItemId.DOLL_서큐버스퀸,
            L1ItemId.DOLL_드레이크,
            L1ItemId.DOLL_킹버그베어,
            L1ItemId.DOLL_다이아몬드골렘
    );

    public final static List<Integer> dollList3Bless = Arrays.asList(
            L1ItemId.DOLL_축자이언트,
            L1ItemId.DOLL_축흑장로,
            L1ItemId.DOLL_축서큐버스퀸,
            L1ItemId.DOLL_축드레이크,
            L1ItemId.DOLL_축킹버그베어,
            L1ItemId.DOLL_축다이아몬드골렘
    );
    public final static List<Integer> dollList3 = new ArrayList<>();

    public final static List<Integer> dollList4Normal = Arrays.asList(
            L1ItemId.DOLL_리치,
            L1ItemId.DOLL_사이클롭스,
            L1ItemId.DOLL_나이트발드,
            L1ItemId.DOLL_시어,
            L1ItemId.DOLL_뱀파이어,
            L1ItemId.DOLL_머미로드
    );

    public final static List<Integer> dollList4Bless = Arrays.asList(
            L1ItemId.DOLL_축리치,
            L1ItemId.DOLL_축사이클롭스,
            L1ItemId.DOLL_축나이트발드,
            L1ItemId.DOLL_축시어,
            L1ItemId.DOLL_축뱀파이어,
            L1ItemId.DOLL_축머미로드
    );
    public final static List<Integer> dollList4 = new ArrayList<>();

    public final static List<Integer> dollList5Normal = Arrays.asList(
            L1ItemId.DOLL_데스,
            L1ItemId.DOLL_데몬,
            L1ItemId.DOLL_바란카,
            L1ItemId.DOLL_타락,
            L1ItemId.DOLL_바포메트,
            L1ItemId.DOLL_얼음여왕,
            L1ItemId.DOLL_커츠,
            L1ItemId.DOLL_파푸,
            L1ItemId.DOLL_안타,
            L1ItemId.DOLL_발라,
            L1ItemId.DOLL_린드
    );

    public final static List<Integer> dollList5Bless = Arrays.asList(
            L1ItemId.DOLL_축데스,
            L1ItemId.DOLL_축데몬,
            L1ItemId.DOLL_축바란카,
            L1ItemId.DOLL_축타락,
            L1ItemId.DOLL_축바포메트,
            L1ItemId.DOLL_축얼음여왕,
            L1ItemId.DOLL_축커츠,
            L1ItemId.DOLL_축파푸,
            L1ItemId.DOLL_축안타,
            L1ItemId.DOLL_축발라,
            L1ItemId.DOLL_축린드
    );

    public final static List<Integer> dollList5 = new ArrayList<>();

    public final static List<Integer> dollList6Normal = Arrays.asList(
            L1ItemId.DOLL_데스,
            L1ItemId.DOLL_데몬,
            L1ItemId.DOLL_바란카,
            L1ItemId.DOLL_타락,
            L1ItemId.DOLL_바포메트,
            L1ItemId.DOLL_얼음여왕,
            L1ItemId.DOLL_파푸,
            L1ItemId.DOLL_안타,
            L1ItemId.DOLL_발라,
            L1ItemId.DOLL_린드
    );

    public final static List<Integer> dollList6Bless = Arrays.asList(
            L1ItemId.DOLL_축데스,
            L1ItemId.DOLL_축데몬,
            L1ItemId.DOLL_축바란카,
            L1ItemId.DOLL_축타락,
            L1ItemId.DOLL_축바포메트,
            L1ItemId.DOLL_축얼음여왕,
            L1ItemId.DOLL_축파푸,
            L1ItemId.DOLL_축안타,
            L1ItemId.DOLL_축발라,
            L1ItemId.DOLL_축린드
    );

    public final static List<Integer> dollList6 = new ArrayList<>();

    public static final Map<String, List<Integer>> dollMap = new HashMap<>();

    static {
        dollList3.addAll(dollList3Normal);
        dollList3.addAll(dollList3Bless);

        dollList4.addAll(dollList4Normal);
        dollList4.addAll(dollList4Bless);

        dollList5.addAll(dollList5Normal);
        dollList5.addAll(dollList5Bless);

        dollList6.addAll(dollList6Normal);
        dollList6.addAll(dollList6Bless);

        dollMap.put("dollList1", dollList1);
        dollMap.put("dollList2", dollList2);

        dollMap.put("dollList3Normal", dollList3Normal);
        dollMap.put("dollList3Bless", dollList3Bless);
        dollMap.put("dollList3", dollList3);

        dollMap.put("dollList4Normal", dollList4Normal);
        dollMap.put("dollList4Bless", dollList4Bless);
        dollMap.put("dollList4", dollList4);

        dollMap.put("dollList5Normal", dollList5Normal);
        dollMap.put("dollList5Bless", dollList5Bless);
        dollMap.put("dollList5", dollList5);

        dollMap.put("dollList6Normal", dollList5Normal);
        dollMap.put("dollList6Bless", dollList5Bless);
        dollMap.put("dollList6", dollList5);
    }
}
