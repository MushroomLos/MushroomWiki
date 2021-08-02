<template>
    <a-layout-footer style="text-align: center">
        MushroomLos Wiki | 欢迎 {{user.name}}
    </a-layout-footer>
</template>

<script lang="ts">
    import { defineComponent, computed } from 'vue';
    import store from "@/store"

    export default defineComponent({
        name: 'the-footer',
        setup(){
            const user = computed(() => store.state.user);

            return{
                user
            }
        }
    });
</script>