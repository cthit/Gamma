import React from "react";
import { Fill, Center } from "../../../../common-ui/layout";
import EditWhitelistItem from "../common-views/edit-whitelist-item";

const AddNewWhitelistItem = ({ whitelistAdd, text }) => (
  <Fill>
    <Center>
      <EditWhitelistItem
        onSubmit={(values, actions) => {
          console.log("hej");
          whitelistAdd(values)
            .then(response => {
              console.log(response);
              actions.resetForm();
            })
            .catch(error => {
              console.log(error);
            });
        }}
        initialValues={{ cid: "" }}
        titleText={text.SaveCidToWhitelist}
        cidInputText={text.Cid}
        fieldRequiredText={text.FieldRequired}
        submitText={text.SaveCid}
      />
    </Center>
  </Fill>
);

export default AddNewWhitelistItem;
