<template>
  <div class="q-pa-md row bg-grey-5" style="border-style: double">
    <q-scroll-area style="width: 100%; height: 500px" ref="chatScroll">
      <q-list style="width: 95%" separator>
        <div v-for="(comment, index) in this.comments" :key="index">
          <q-item
            :class="
              comment.hasOwnProperty('bgColor') ? comment.bgColor : 'bg-grey-3'
            "
            style="border-style: solid; max-width: 100%"
          >
            <q-item-section>
              <q-item-label
                class="text-weight-bold"
                style="text-decoration-line: underline"
                >{{ comment.responseBy }} :</q-item-label
              >
              <q-item-label caption lines="2" :id="`el${index}`">
                <span v-html="comment.content"></span>
              </q-item-label>
            </q-item-section>
            <q-item-section side top>
              <q-item-label caption>{{
                this.getTimeAgo(comment.responseDate)
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
      <div class="row justify-between">
        <span class="text-weight-bold" style="text-decoration-line: underline"
        >Write a Comment:</span>
    </div>
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
        :toolbar="[['bold', 'italic', 'strike', 'underline','removeFormat'],  [
          {
            label: $q.lang.editor.formatting,
            icon: $q.iconSet.editor.formatting,
            list: 'no-icons',
            options: [
              'p',
              'h1',
              'h2',
              'h3',
              'h4',
              'h5',
              'h6',
              'code'
            ]
          },
          {
            label: $q.lang.editor.fontSize,
            icon: $q.iconSet.editor.fontSize,
            fixedLabel: true,
            fixedIcon: true,
            list: 'no-icons',
            options: [
              'size-1',
              'size-2',
              'size-3',
              'size-4',
              'size-5',
              'size-6',
              'size-7'
            ]
          }],['send']]"
        min-height="5rem"
      >
      </q-editor>
    </div>
  </div>
</template>

<script>
import moment from "moment";
import { DateTime } from "luxon";
import { useUserStore } from "src/stores/user";

export default {
  props: ["ticketId", "comments"],
  emits: ["add-comment", 'ack-comment', 'unack-comment'],
  data: () => {
    return {
      editor: "",
      ack:null,
    };
  },
  computed: {
  },
  mounted() {
    this.scrollToBottom();
  },
  methods: {
    //input iso 8601 format
    getTimeAgo(date) {
      const luxonDateTime = DateTime.fromISO(date);
      const jsDate = luxonDateTime.toJSDate();
      const momentObj = moment(jsDate);
      const fromNowTime = momentObj.fromNow();
      return fromNowTime;
    },
    handleSendComment() {
      const date = new Date().toISOString();
      const comment = {
        responseDate: date,
        content: this.editor,
        moOID: this.ticketId,
      };
      this.$emit("add-comment", comment);
    },
    checkAckPermission() {
      return useUserStore().checkPermission("ticketing.edit.acknowledge");
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
