package com.krest.sorm.tools;

import com.krest.sorm.conn.DBManager;
import com.krest.sorm.properties.ColumnInfo;
import com.krest.sorm.properties.JavaFieldGetSet;
import com.krest.sorm.properties.TableInfo;
import com.krest.sorm.properties.TypeConvevrtor;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JavaFileUtils {

    /**
     * 根据字段信息生成Java属性信息 如var name -》 private String userName，以及相应的 Get和 Set方法
     *
     * @param columnInfo 字段信息
     * @param convevrtor 转换器
     * @return
     */
    public static JavaFieldGetSet createFieldGetSet(ColumnInfo columnInfo, TypeConvevrtor convevrtor) {
        // 用来封装每个字段对应的String信息
        JavaFieldGetSet jfgs = new JavaFieldGetSet();
        // 封转字段信息
        String name = columnInfo.getName();
        String sName = StringUtils.replaceStringSign(name);
        String javaFieldType = convevrtor.databaseType2JavaType(columnInfo.getDataType());

        // 行首添加制表符，行尾添加换行符，拼接字符串
        jfgs.setFieldInfo("\tprivate " + javaFieldType + " " + sName + ";\n");

        // 生成相应的Get 如 public String getYUSerName(){return username}
        StringBuilder getStr = new StringBuilder();
        String sNameToString = sName.toString();
        getStr.append("\tpublic " + javaFieldType + " get" + StringUtils.firstChar2UpperCase(sNameToString) + "(){\n");
        getStr.append("\t\treturn " + sName + ";\n");
        getStr.append("\t}\n");
        jfgs.setGetInfo(getStr.toString());

        //// 生成相应的Set 如 public String getYUSerName(){return username}
        StringBuilder setStr = new StringBuilder();
        setStr.append("\tpublic void set" + StringUtils.firstChar2UpperCase(sNameToString) + "(");
        setStr.append(javaFieldType + " " + sName + "){\n");
        setStr.append("\t\tthis." + sName + "=" + sName + ";\n");
        setStr.append("\t}\n");
        jfgs.setSetInfo(setStr.toString());
        return jfgs;
    }

    /**
     * 根据表信息，生成Java类的源码信息
     *
     * @param tableInfo  表信息
     * @param convevrtor 数据类类型转化器
     * @return Java类的源码String字段
     */
    public static String createJavaSrc(TableInfo tableInfo, TypeConvevrtor convevrtor) {
        StringBuilder srcStr = new StringBuilder();

        Map<String, ColumnInfo> columns = tableInfo.getColumns();
        List<JavaFieldGetSet> javaFields = new ArrayList<>();

        // 得到所有的表信息的Set/Get方法
        for (ColumnInfo c : columns.values()) {
            javaFields.add(createFieldGetSet(c, convevrtor));
        }
        ;

        // 生成entity   Package语句
        srcStr.append("package " + DBManager.getConf().getPoPackage() + ".entity;\n\n");

        // 生成import语句
        srcStr.append("import java.util.Date;\n");
        srcStr.append("import lombok.AllArgsConstructor;\n");
        srcStr.append("import lombok.Data;\n");
        srcStr.append("import lombok.NoArgsConstructor;\n\n\n");

        // 生成注解
        srcStr.append("@Data\n");
        srcStr.append("@NoArgsConstructor\n");
        srcStr.append("@AllArgsConstructor\n");

        // 生成类名的声明语句
        srcStr.append("public class " + StringUtils.firstChar2UpperCase(StringUtils.replaceStringSign(tableInfo.getTname())) + " {\n");

        // 生成属性列表
        for (JavaFieldGetSet javaField : javaFields) {
            srcStr.append(javaField.getFieldInfo());
        }
        // 添加换行符号
        srcStr.append("\n\n");

        //  生成get方法列表
        for (JavaFieldGetSet javaField : javaFields) {
            srcStr.append(javaField.getGetInfo());
        }
        srcStr.append("\n\n");

        //  生成Set方法列表
        for (JavaFieldGetSet javaField : javaFields) {
            srcStr.append(javaField.getSetInfo());
        }
        srcStr.append("\n\n");

        // 生成结束
        srcStr.append("}\n");
        return srcStr.toString();
    }

    /**
     * 新建Java实体类文件
     *
     * @param tableInfo
     * @param convevrtor
     */
    public static void createJavaPOFile(TableInfo tableInfo, TypeConvevrtor convevrtor) {

        String src = createJavaSrc(tableInfo, convevrtor);
        // 通过流，将生成的String写入到文件中
        StringBuilder srcFinalPath = new StringBuilder();
        // 获取项目目录
        String srcPath = DBManager.getConf().getSrcPath();
        // 获取包路径
        String poPackage = DBManager.getConf().getPoPackage();
        System.out.println(DBManager.getConf().toString());
        String[] poPackages = poPackage.split("\\.");

        // 对包路径进行格式化
        srcFinalPath.append(srcPath);
        for (String aPackage : poPackages) {
            srcFinalPath.append("\\" + aPackage);
        }

        // 生成实体类路径地址
        String entityFilePath = srcFinalPath.append("\\entity").toString();
        File entityFile = new File(entityFilePath);
        if (!entityFile.exists()) {
            boolean mkdir = entityFile.mkdir();
        }

        BufferedWriter bw = null;

        try {
            String entityFileName = entityFile.getAbsoluteFile() + "\\" + StringUtils.firstChar2UpperCase(StringUtils.replaceStringSign(tableInfo.getTname())) + ".java";
            File f = new File(entityFileName);
            if (!f.isFile() && !f.exists()) {
                System.out.println("************创建Java文件");
                bw = new BufferedWriter(new FileWriter(entityFileName));
                bw.write(src);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bw != null) {
                try {
                    bw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
