<template>
  <div style="margin:auto;text-align: center">
    <el-upload
        ref="upload"
        class="upload-demo"
        :limit="1"
        action=""
        :on-change="handleChange"
        :on-exceed="handleExceed"
        :auto-upload="false"
    >
      <template #trigger>
        <el-button type="primary">选择文件</el-button>
      </template>
      <el-button class="ml-3" type="success" @click="submitUpload">
        确认上传
      </el-button>
      <template #tip>
        <div class="el-upload__tip text-red">
          限制上传1个文件,新文件覆盖旧文件
        </div>
      </template>
    </el-upload>
    <el-progress type="circle" :percentage="percent" :color="colors" :status="status"/>
  </div>
</template>
<script lang="ts" setup>
import {ref} from 'vue'
import type {UploadInstance, UploadProps, UploadRawFile} from 'element-plus'
import axios from "axios";
import {genFileId} from "element-plus";

const upload = ref<UploadInstance>()
const colors = [
  { color: '#f56c6c', percentage: 0 },
  { color: '#e6a23c', percentage: 50 },
  { color: '#5cb87a', percentage: 100 }
]
const percent = ref(0)

let uploadData = {}
let totalChunks = 0
let chunkSize = 10 * 1024 * 1024
let file = null
let status: string = "";

const handleExceed: UploadProps['onExceed'] = (files) => {
  upload.value!.clearFiles()
  const file = files[0] as UploadRawFile
  file.uid = genFileId()
  upload.value!.handleStart(file)
}

const submitUpload = async () => {
  percent.value = 1
  const concurrentLimit = 3; // 并发限制数
  let tasks = []; // 上传任务数组
  let uploaded = 0; // 已上传块数

  for (let i = 0; i < totalChunks; i++) {
    let start = i * chunkSize;
    let end = Math.min(file.size, start + chunkSize);

    let chunk = file.raw.slice(start, end);
    tasks.push(
        new Promise((resolve, reject) => {
          axios({
            url: uploadData["uploadUrls"]["chunk_" + i],
            method: "put",
            data: chunk ,
          })
              .then(() => {
                percent.value = Math.floor(
                    (uploaded / totalChunks) * 100
                );
                uploaded++;
                resolve();
              })
              .catch((error) => {
                reject(error);
              });
        })
    );

    if (tasks.length == concurrentLimit) {
      await Promise.all(tasks);
      tasks = [];
    }
  }
  if (tasks.length > 0) {
    await Promise.all(tasks);
  }
  let url =
      "http://127.0.0.1:8080/minio/completeUpload?uploadId=" + uploadData["uploadId"] + "&objectName=" + uploadData["objectName"];
  const res = await axios.get(url);
  console.log(res)
  percent.value = 100;
  status = "success"
}

const handleChange: UploadProps['onChange'] = (uploadFile, uploadFiles) => {
  file = uploadFile;
  totalChunks = Math.ceil(uploadFile.size / chunkSize);
  console.log(file.name, "切片数量: ", totalChunks);
  let url = "http://127.0.0.1:8080/minio/getUploadUrl?fileName=" + file.name +"&totalChunk=" +totalChunks;
  axios
      .get(url)
      .then((res) => {
        console.log(res);
        uploadData = res.data;
      })
      .catch((e) => {
        console.log(e);
      });
}
</script>
