package com.example.antiquepedia_backend.utils;

import org.apache.jena.graph.Node;
import org.apache.jena.reasoner.rulesys.RuleContext;
import org.apache.jena.reasoner.rulesys.builtins.BaseBuiltin;

// 用于规则推理中 计算 文本相似度的 函数
public class SemanticSimilarityBuiltin extends BaseBuiltin {
    // 函数名字
    @Override
    public String getName() {
        return "semsim";
    }

    // 接收参数
    @Override
    public int getArgLength(){
        return 3;
    }

    // 编写实际的 逻辑
    @Override
    public boolean bodyCall(Node[] args, int length, RuleContext context){
        checkArgs(length, context);
        Node n1 = getArg(0, args, context);
        Node n2 = getArg(1, args, context);
        Node score = getArg(2,args,context);

        if(!score.isLiteral()  || score.getLiteral().getValue()==null){
            return false;
        }
        Double hold = Double.parseDouble(score.getLiteralValue().toString());

        if (n1.isLiteral() && n2.isLiteral()) {
            String v1 = n1.getLiteralValue().toString();
            String v2 = n2.getLiteralValue().toString();

            // 调用服务计算相似度 这里先默认就是 0.7
            double similarity = 0.7;
            if(similarity > hold){
                return true;
            }

            return false;
        }

        return false;
    }
}
