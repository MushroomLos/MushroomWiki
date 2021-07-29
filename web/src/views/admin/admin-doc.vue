<template>
  <a-layout>
    <a-layout-content
            :style="{ background: '#fff', padding: '24px', margin: 0, minHeight: '280px' }"
    >
      <p>
        <a-form layout="inline" :model="param">
          <a-form-item>
            <a-button type="primary" @click="handleQuery()">
              查询
            </a-button>
          </a-form-item>
          <a-form-item>
            <a-button type="primary" @click="add()">
              新增
            </a-button>
          </a-form-item>
        </a-form>
      </p>
      <a-table
        :columns="columns"
        :row-key="record => record.id"
        :data-source="level1"
        :loading="loading"
        :pagination="false"
      >
        <template #cover="{text:cover}">
          <img v-if="cover" :src="cover" alt="avatar" />
        </template>
        <template v-slot:action="{ text, record }">
          <a-space size="small">
            <a-button type="primary" @click="edit(record)">
              编辑
            </a-button>
            <a-button type="danger" @click="handleDelete(record.id)">
              删除
            </a-button>
          </a-space>
        </template>
      </a-table>
    </a-layout-content>
  </a-layout>

  <a-modal
          title="文档表单"
          v-model:visible="modalVisible"
          :confirm-loading="modalLoading"
          @ok="handleModalOk"
  >
    <a-form :model="doc" :label-col="{ span: 6 }" :wrapper-col="{ span: 18 }">
      <a-form-item label="名称">
        <a-input v-model:value="doc.name"/>
      </a-form-item>
      <a-form-item label="父文档">
        <a-select
                v-model:value="doc.parent"
                ref="select"
        >
          <a-select-option value="0">
            无
          </a-select-option>
          <a-select-option v-for="c in level1" :key="c.id" :value="c.id" :disabled="doc.id === c.id">
            {{c.name}}
          </a-select-option>
        </a-select>
      </a-form-item>
      <a-form-item label="顺序">
        <a-input v-model:value="doc.sort"/>
      </a-form-item>
    </a-form>
  </a-modal>
</template>

<script lang="ts">
  import {defineComponent, onMounted, ref} from 'vue';
  import axios from 'axios';
  import {message} from 'ant-design-vue';
  import { Tool } from '@/util/tool';

  export default defineComponent({
    name: 'AdminDoc',
    setup(){
      const param = ref();
      param.value = {};
      const docs = ref();
      const loading = ref(false);

      const columns = [
        {
          title: '名称',
          dataIndex: 'name'
        },
        {
          title: '父文档',
          key: 'parent',
          dataIndex: 'parent'
        },
        {
          title: '顺序',
          dataIndex: 'sort'
        },
        {
          title: 'Action',
          key: 'action',
          slots: {customRender: 'action'}
        }
      ];

      /**
       * Data querying
       * 一级文档树，children属性是二级文档树
       * [{
       *     id: "",
       *     name: "",
       *     children: [{
       *         id: "",
       *         name: ""
       *     }]
       * }]
       */

      const level1 = ref();

      const handleQuery = () => {
        loading.value = true;
        axios.get("/doc/all",).then((response) =>{
          loading.value = false;
          const data = response.data;
          if(data.success){
            docs.value = data.content;
            console.log("原始数据:", docs.value);

            level1.value = [];
            level1.value = Tool.array2Tree(docs.value, 0);
            console.log("树型结构:", level1);
          }else{
            message.error(data.message);
          }
        });
      };



      // --表单--
      const doc = ref({});
      const modalVisible = ref(false);
      const modalLoading = ref(false);
      const handleModalOk = () =>{
        modalLoading.value = true;
        axios.post("/doc/save", doc.value).then((response) =>{
          modalLoading.value = false;
          const data = response.data; // data = commonResp
          if(data.success){
            modalVisible.value = false;

            // reloading
            handleQuery();
          }else{
            message.error(data.message);
          }
        });
      };

      /**
       * 编辑
       */
      const edit = (record: any) =>{
        modalVisible.value = true;
        doc.value = Tool.copy(record);
      };

      /**
       * 新增
       */
      const add = () =>{
        modalVisible.value = true;
        doc.value = {};
      };

      /**
       * 删除
       */
      const handleDelete = (id: number) =>{
        axios.delete("/doc/delete/" + id).then((response) =>{
          const data = response.data; // data = commonResp
          if(data.success){
            // reloading
            handleQuery();
          }
        });
      };

      onMounted(() => {
        handleQuery();
      });

      return{
        param,
        // docs,
        level1,
        columns,
        loading,
        handleQuery,

        edit,
        add,
        handleDelete,

        doc,
        modalVisible,
        modalLoading,
        handleModalOk
      }
    }
  });
</script>