<template>
  <a-layout>
    <a-layout-content
            :style="{ background: '#fff', padding: '24px', margin: 0, minHeight: '280px' }"
    >
      <p>
        <a-form layout="inline" :model="param">
          <a-form-item>
            <a-input v-model:value="param.name" placeholder="名称">
            </a-input>
          </a-form-item>
          <a-form-item>
            <a-button type="primary" @click="handleQuery({page: 1, size: pagination.pageSize})">
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
        :data-source="categorys"
        :pagination="pagination"
        :loading="loading"
        @change="handleTableChange"
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
          title="分类表单"
          v-model:visible="modalVisible"
          :confirm-loading="modalLoading"
          @ok="handleModalOk"
  >
    <a-form :model="category" :label-col="{ span: 6 }" :wrapper-col="{ span: 18 }">
      <a-form-item label="名称">
        <a-input v-model:value="category.name"/>
      </a-form-item>
      <a-form-item label="父分类">
        <a-input v-model:value="category.parent"/>
      </a-form-item>
      <a-form-item label="顺序">
        <a-input v-model:value="category.sort"/>
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
    name: 'AdminCategory',
    setup(){
      const param = ref();
      param.value = {};
      const categorys = ref();
      const pagination = ref({
        current: 1,
        pageSize: 10,
        total: 0
      });
      const loading = ref(false);

      const columns = [
        {
          title: '名称',
          dataIndex: 'name'
        },
        {
          title: '父分类',
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
       */
      const handleQuery = (params: any) => {
        loading.value = true;
        axios.get("/category/list", {
          params: {
            page: params.page,
            size: params.size,
            name: param.value.name
          }
        }).then((response) =>{
          loading.value = false;
          const data = response.data;
          if(data.success){
            categorys.value = data.content.list;

            // 重置分页按钮
            pagination.value.current = params.page;
            pagination.value.total = data.content.total;
          }else{
            console.log(data.success);
            message.error(data.message);
          }
        });
      };

      /**
       * trigger at clicking the pageNumber
       */
      const handleTableChange = (pagination: any) =>{
        console.log("View what params have: " + pagination);
        handleQuery({
          page: pagination.current,
          size: pagination.pageSize
        });
      };

      // --表单--
      const category = ref({});
      const modalVisible = ref(false);
      const modalLoading = ref(false);
      const handleModalOk = () =>{
        modalLoading.value = true;
        axios.post("/category/save", category.value).then((response) =>{
          modalLoading.value = false;
          const data = response.data; // data = commonResp
          if(data.success){
            modalVisible.value = false;

            // reloading
            handleQuery({
              page: pagination.value.current,
              size: pagination.value.pageSize,
            });
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
        category.value = Tool.copy(record);
      };

      /**
       * 新增
       */
      const add = () =>{
        modalVisible.value = true;
        category.value = {};
      };

      /**
       * 删除
       */
      const handleDelete = (id: number) =>{
        axios.delete("/category/delete/" + id).then((response) =>{
          const data = response.data; // data = commonResp
          if(data.success){
            // reloading
            handleQuery({
              page: pagination.value.current,
              size: pagination.value.pageSize,
            });
          }
        });
      };

      onMounted(() => {
        handleQuery({
          page: 1,
          size: pagination.value.pageSize,
        });
      });

      return{
        param,
        categorys,
        pagination,
        columns,
        loading,
        handleTableChange,
        handleQuery,

        edit,
        add,
        handleDelete,

        category,
        modalVisible,
        modalLoading,
        handleModalOk
      }
    }
  });
</script>