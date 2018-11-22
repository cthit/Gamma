import React from "react";
import WhitelistItemForm from "../common-views/whitelist-item-form";

import { DigitTranslations, DigitLayout } from "@cthit/react-digit-components";
import translations from "./AddNewWhitelistItem.screen.translations.json";

const AddNewWhitelistItem = ({ whitelistAdd }) => (
  <DigitTranslations
    translations={translations}
    uniquePath="Whitelist.Screen.AddNewWhitelistItem"
    render={text => (
      <DigitLayout.Fill>
        <DigitLayout.Center>
          <WhitelistItemForm
            onSubmit={(values, actions) => {
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
        </DigitLayout.Center>
      </DigitLayout.Fill>
    )}
  />
);

export default AddNewWhitelistItem;
