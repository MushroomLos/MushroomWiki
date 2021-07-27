<template>
  <a-layout>
    <a-layout-content
            :style="{ background: '#fff', padding: '24px', margin: 0, minHeight: '280px' }"
    >
      <a-table
        :columns="columns"
        :row-key="record => record.id"
        :data-source="ebooks"
        :pagination="pagination"
        :loading="loading"
        @change="handleTableChange"
      >
        <template #cover="{text:cover}">
          <img v-if="cover" :src="cover" alt="avatar" />
        </template>
        <template v-slot:action="{ text, record }">
          <a-space size="small">
            <a-button type="primary">
              编辑
            </a-button>
            <a-button type="danger">
              删除
            </a-button>
          </a-space>
        </template>
      </a-table>
    </a-layout-content>
  </a-layout>
</template>

<script lang="ts">
  import {defineComponent, onMounted, ref} from 'vue';
  import axios from 'axios';

  export default defineComponent({
    name: 'AdminEbook',
    setup(){
      const ebooks = ref();
      const pagination = ref({
        current: 1,
        pageSize: 2,
        total: 0
      });
      const loading = ref(false);

      const columns = [
        {
          title: 'Cover',
          dataIndex: 'cover',
          slots: {customRender: 'cover'}
        },
        {
          title: 'Name',
          dataIndex: 'name'
        },
        {
          title: 'Category_1',
          key: 'category1Id',
          dataIndex: 'category1Id'
        },
        {
          title: 'Category_2',
          key: 'category2Id',
          dataIndex: 'category2Id'
        },
        {
          title: 'Doc',
          dataIndex: 'doc_count'
        },
        {
          title: 'View',
          dataIndex: 'view_count'
        },
        {
          title: 'Vote',
          dataIndex: 'vote_count'
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
        axios.get("/ebook/list", params).then((response) =>{
          loading.value = false;
          const data = response.data;
          ebooks.value = data.content;

          // 重置分页按钮
          pagination.value.current = params.page;
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

      onMounted(() => {
        handleQuery({});
      });

      return{
        ebooks,
        pagination,
        columns,
        loading,
        handleTableChange
      }
    }
  });
</script>