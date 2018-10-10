import React from "react";
import { Fill, Center } from "../../../../common-ui/layout";
import WhitelistItemForm from "../common-views/whitelist-item-form";

import GammaTranslations from "../../../../common/declaratives/gamma-translations";
import translations from "./AddNewWhitelistItem.screen.translations.json";

const AddNewWhitelistItem = ({ whitelistAdd }) => (
  <GammaTranslations
    translations={translations}
    uniquePath="Whitelist.Screen.AddNewWhitelistItem"
    render={text => (
      <Fill>
        <Center>
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
        </Center>
      </Fill>
    )}
  />
);

export default AddNewWhitelistItem;
