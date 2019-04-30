package cn.posinda.phoenix.controller;

import cn.posinda.base.WebResult;
import cn.posinda.phoenix.tools.ArgumentValidation;
import cn.posinda.utils.HdfsUtil;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/hdfs")
public class HdfsController {

    @Resource
    private HdfsUtil hdfsUtil;

    private static String SUCCESS = "操作成功";

    private static String FAILED = "操作失败";


    /**
     * 创建新的文件夹，需要包含文件夹的完整路径名称，由用户名和具体的文件夹名构成，以/+用户名开始，例如：/LILY/EX
     * 本方法会创建需要但本来不存在的目录
     *
     * @param dirName 需要创建的文件夹名称
     */
    @PostMapping(value = "/mkDir")
    public WebResult mkDir(String dirName) {
        ArgumentValidation.checkNotEmpty(new String[]{dirName});
        final boolean result = hdfsUtil.createDir(dirName.trim());
        return WebResult.success().put("result", result ? SUCCESS : FAILED);
    }

    /**
     * 文件的重命名
     *
     * @param userLocation   文件所在的文件夹，由用户名和具体的文件夹名构成，例如：/LILY/EX,如果文件直接放在
     *                       用户的根目录，此文件夹名只包含用户名，例如：/LILY，传入的参数不能为空，指定的新文件名不能已经存在
     * @param formerFileName 文件原来的名称
     * @param newFileName    文件的新名称
     */
    @PostMapping(value = "/fileRename")
    public WebResult fileRename(String userLocation,
                                String formerFileName,
                                String newFileName) {
        ArgumentValidation.checkNotEmpty(new String[]{userLocation, formerFileName, newFileName});
        final boolean result = hdfsUtil.renameFile(formerFileName.trim(), userLocation.trim(), newFileName.trim());
        return WebResult.success().put("result", result ? SUCCESS : FAILED);
    }

    /**
     * 文件夹的重命名
     *
     * @param formerDirName 需要重命名的文件夹，由用户名和具体的文件夹名构成，例如：/LILY/EX
     * @param newDirName    新的文件夹名，原文件夹名和新的文件夹名必须含有相同的层级
     */
    @PostMapping(value = "/dirRename")
    public WebResult dirRename(String formerDirName,
                               String newDirName) {
        ArgumentValidation.checkNotEmpty(new String[]{formerDirName, newDirName});
        final boolean result = hdfsUtil.renameDir(formerDirName.trim(), newDirName.trim());
        return WebResult.success().put("result", result ? SUCCESS : FAILED);
    }

    /**
     * 文件移动，文件移动的目标文件夹必须已经存在且在目标文件夹中不能存在同名的文件或者文件夹名称
     * 暂时不支持文件夹的整体移动,本方法的实现为在目标文件夹中创建相同的文件名，通过文件的复制，然后
     * 删除原本的文件，如果文件较大则耗时较长，最好只进行较小文件的移动
     *
     * @param formerDir 文件所在文件夹的完全路径
     * @param fileName  需要移动的文件
     * @param newDir    目标文件夹
     */
    @PostMapping(value = "/moveFile")
    public WebResult moveFile(String formerDir,
                              String fileName,
                              String newDir) {
        ArgumentValidation.checkNotEmpty(new String[]{formerDir, fileName, newDir});
        final boolean result = hdfsUtil.moveFile(formerDir.trim(), fileName.trim(), newDir.trim());
        return WebResult.success().put("result", result ? SUCCESS : FAILED);
    }

    /**
     * 删除指定的文件
     *
     * @param dirName  需要删除的文件所在的文件夹的全路径名
     * @param fileName 需要删除的文件名称
     */
    @DeleteMapping(value = "/deleteFile")
    public WebResult deleteFile(String dirName,
                                String fileName) {
        ArgumentValidation.checkNotEmpty(new String[]{dirName, fileName});
        final boolean result = hdfsUtil.deleteFile(dirName.trim(), fileName.trim());
        return WebResult.success().put("result", result ? SUCCESS : FAILED);
    }


    /**
     * 删除指定的文件夹，注意：如果文件夹不为空，那么此操作将会删除文件夹下的所有内容,注意，通过命令行删除的文件
     * 或者文件夹可以通过垃圾桶的方式恢复，但是通过API的方式数据将不能恢复，在确保文件夹为空或者文件夹的所有文件和
     * 目录都不需要的时候再进行目录的删除。
     *
     * @param dirName 需要删除的文件夹的全路径名
     */
    @DeleteMapping(value = "/deleteDir")
    public WebResult deleteDir(String dirName) {
        ArgumentValidation.checkNotEmpty(new String[]{dirName});
        final boolean result = hdfsUtil.deleteDir(dirName.trim());
        return WebResult.success().put("result", result ? SUCCESS : FAILED);
    }

    /**
     * 显示指定文件夹下的文件和目录,以及相应的属性信息
     * 包含名称，文件长度，最后修改时间，最后访问时间，是否是文件夹等，目录的文件长度为0
     *
     * @param dirName  指定的文件夹的全路径名,以/开头，如果需要查找指定用户根目录下的文件列表，dirName=/
     * @param userName 用户名称
     */
    @GetMapping(value = "/listFilesInSpecifiedDir")
    public WebResult listFilesInSpecifiedDir(String dirName,
                                             String userName) {
        ArgumentValidation.checkNotEmpty(new String[]{dirName, userName});
        final SimpleFileStatus[] filesInSpecifiedDir = hdfsUtil.getFilesInSpecifiedDir(dirName.trim(), userName.trim());
        return WebResult.success().put("result", filesInSpecifiedDir);
    }

}
