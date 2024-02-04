<template>
  <div class="d-flex align-center flex-column">
    <v-card width="100%" max-width="600px">
      <v-card-title>
        Стартовая страница
      </v-card-title>
      <v-card-text>
        <div>Здравствуйте, {{ user.description }}!</div>
        <div>Вы залогинились как {{ userTypeTxt }}.</div>
        <div>Вам {{ userAccessTxt }} магазинов, товаров и их цен в магазинах.</div>
      </v-card-text>
    </v-card>
  </div>
</template>

<script setup lang="ts">
import {useUser} from "~/stores/user";

const user = useUser()

const
    userTypeTxt = computed<string>(() => user.isAdmin ? 'администратор' : 'обычный пользователь'),
    userAccessTxt = computed<string>(() => user.isAdmin ? 'доступны просмотр и редактирование' : 'доступен просмотр')
</script>
