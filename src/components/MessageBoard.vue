<template>
    <div class="q-pa-md row justify-center">
    <div style="width: 100%; max-width: 400px">
        <div v-for="(comment, index) in this.comments" :key="index">
            <q-chat-message
                :name="comment.responseBy"
                stamp="7 minutes ago"
                sent
                bg-color="amber-7"
            >
            <div>
                {{comment.content}}
            </div>
        </q-chat-message>
      </div>
    </div>
  </div>
</template>

<script>
export default{
    data:() => {
        return {
            comments:[{responseBy:"p1", content:"c1"}, {responseBy:"p2", content:"c2"}]
        }
    },
}
</script>

<style>

</style>