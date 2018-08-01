import React from "react";
import EditWhitelistItem from "../common-views/edit-whitelist-item/EditWhitelistItem.view";
import translations from "./ValidateCid.screen.translations.json";
import GammaTranslations from "../../../../common/declaratives/gamma-translations";
import { Center } from "../../../../common-ui/layout";

const ValidateCid = ({ whitelistValidate, toastOpen }) => (
  <GammaTranslations
    translations={translations}
    uniquePath="Whitelist.Screen.ValdiateCid"
    render={text => (
      <Center>
        <EditWhitelistItem
          initialValues={{ cid: "" }}
          onSubmit={(values, actions) => {
            whitelistValidate(values.cid)
              .then(response => {
                if (response) {
                  toastOpen({
                    text: text.CidIsValid
                  });
                } else {
                  toastOpen({
                    text: text.CidIsNotValid
                  });
                }
              })
              .catch(error => {
                toastOpen({
                  text: text.SomethingWentWrong,
                  duration: 10000
                });
              });
          }}
          titleText={text.ValidateCid}
          cidInputText={text.CidToValidate}
          fieldRequiredText={text.FieldRequired}
          submitText={text.Validate}
        />
      </Center>
    )}
  />
);

export default ValidateCid;
