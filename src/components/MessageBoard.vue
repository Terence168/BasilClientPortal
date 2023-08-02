<template>
  <span
    class="text-weight-bold text-subtitle1"
    style="text-decoration-line: underline"
    >Comments</span
  >
  <div class="q-pa-md row bg-grey-5" style="border-style: double">
    <q-scroll-area style="width: 100%; height: 500px" ref="chatScroll">
      <q-list style="width: 95%" separator>
        <div v-for="(comment, index) in this.comments" :key="index">
          <q-item
            class="bg-grey-3"
            style="border-style: solid; max-width: 100%"
          >
            <q-item-section>
              <q-item-label
                class="text-weight-bold"
                style="text-decoration-line: underline"
                >{{ comment.responseBy }} :</q-item-label
              >
              <q-item-label caption lines="2">{{
                comment.content
              }}</q-item-label>
            </q-item-section>
            <q-item-section side top>
              <q-item-label caption>{{
                this.getTimeAgo(comment.date)
              }}</q-item-label>
            </q-item-section>
          </q-item>
          <q-separator spaced inset></q-separator>
        </div>
        <div class="q-pa-md q-gutter-sm"></div>
        <!-- </q-scroll-area> -->
      </q-list>
    </q-scroll-area>
    <!-- text input editor -->
    <div style="width: 95%">
      <span class="text-weight-bold" style="text-decoration-line: underline"
        >Write a Comment:</span
      >
      <q-editor
        v-model="editor"
        :definitions="{
          send: {
            tip: 'reply',
            icon: 'reply',
            label: 'send',
            handler: handleSendComment,
          },
        }"
        :toolbar="[['bold', 'italic', 'strike', 'underline'], ['send']]"
        min-height="5rem"
      >
      </q-editor>
    </div>
  </div>
</template>

<script>
import moment from "moment";
import { useUserStore } from "stores/user";

const user = useUserStore();

export default {
  props: ["ticketId"],
  data: () => {
    return {
      //supposed to be send by parent component
      comments: [
        { responseBy: "p1", content: "c1", date: "2023-03-08 10:30:00" },
        { responseBy: "p2", content: "c2", date: "2023-02-08 09:30:00" },
        { responseBy: "p1", content: "c1", date: "2023-03-08 10:30:00" },
        { responseBy: "p2", content: "c2", date: "2023-02-08 09:30:00" },
        { responseBy: "p1", content: "c1", date: "2023-03-08 10:30:00" },
        { responseBy: "p2", content: "c2", date: "2023-02-08 09:30:00" },
        { responseBy: "p1", content: "c1", date: "2023-03-08 10:30:00" },
        { responseBy: "p2", content: "c2", date: "2023-02-08 09:30:00" },
        { responseBy: "p1", content: "c1", date: "2023-03-08 10:30:00" },
        { responseBy: "p2", content: "c2", date: "2023-02-08 09:30:00" },
        { responseBy: "p1", content: "c1", date: "2023-03-08 10:30:00" },
        { responseBy: "p2", content: "c2", date: "2023-02-08 09:30:00" },
      ],
      editor: null,
    };
  },
  mounted() {
    this.scrollToBottom();
  },
  methods: {
    getTimeAgo(date) {
      const timeAgo = moment(date, "YYYY-MM-DD hh:mm:ss", true).fromNow();
      return timeAgo;
    },
    handleSendComment() {
      const content = this.editor;
      const { username, email } = user;
      const date = new Date();
      //TODO: confirm what's the responseBy info
      const newComment = {
        content,
        mo_oid: this.ticketId,
      };
      this.comments.push(newComment);

      const vm = this;
      const link = `/ticketing/${this.ticketId}/comments`;
    //   api
    //     .post(link, newComment)
    //     .then((response) => {
    //       if (response.data.resultCode !== 0) {
    //         throw new Error(response.data.errorMessage);
    //       }
    //     })
    //     .catch((error) => {
    //       console.log(error);
    //       Notify.create({
    //         type: "negative",
    //         message: error.message,
    //       }).finally(() => {
    //         vm.scrollToBottom();
    //       });
    //     });
    },
    scrollToBottom() {
      const scrollArea = this.$refs.chatScroll;
      const scrollTarget = scrollArea.getScrollTarget();
      const duration = 0; // ms - use 0 to instant scroll
      scrollArea.setScrollPosition(
        "vertical",
        scrollTarget.scrollHeight,
        duration
      );
    },
  },
};
</script>

<style></style>
