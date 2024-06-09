package com.planb.utils;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.SingularValueDecomposition;
import java.util.*;

public class SVDRecommendation {

    private RealMatrix userAdMatrix;
    private List<String> userIds;
    private List<String> adIds;

    public SVDRecommendation(double[][] data, List<String> userIds, List<String> adIds) {
        this.userAdMatrix = MatrixUtils.createRealMatrix(data);
        this.userIds = userIds;
        this.adIds = adIds;
    }

    public RealMatrix computeSVD(int k) {
        SingularValueDecomposition svd = new SingularValueDecomposition(userAdMatrix);
        RealMatrix U = svd.getU();
        RealMatrix S = MatrixUtils.createRealDiagonalMatrix(Arrays.copyOfRange(svd.getSingularValues(), 0, k));
        RealMatrix V = svd.getVT();
        RealMatrix U_k = U.getSubMatrix(0, U.getRowDimension() - 1, 0, k - 1);
        RealMatrix V_k = V.getSubMatrix(0, k - 1, 0, V.getColumnDimension() - 1);
        return U_k.multiply(S).multiply(V_k);
    }

    public List<String> recommend(String userId, int numRecommendations) {
        int userIndex = userIds.indexOf(userId);
        RealMatrix predictedMatrix = computeSVD(10);
        double[] userRatings = predictedMatrix.getRow(userIndex);

        List<Map.Entry<Integer, Double>> adRatings = new ArrayList<>();
        for (int i = 0; i < userRatings.length; i++) {
            adRatings.add(new AbstractMap.SimpleEntry<>(i, userRatings[i]));
        }

        adRatings.sort((e1, e2) -> Double.compare(e2.getValue(), e1.getValue()));
        List<String> recommendedAds = new ArrayList<>();
        for (int i = 0; i < numRecommendations && i < adRatings.size(); i++) {
            recommendedAds.add(adIds.get(adRatings.get(i).getKey()));
        }

        return recommendedAds;
    }
}

