import {
  DigitButton,
  DigitTranslations,
  DigitDisplayData,
  DigitDesign,
  DigitLayout,
  DigitIfElseRendering
} from "@cthit/react-digit-components";
import React from "react";
import translations from "./ShowUserDetails.screen.translations.json";

const ShowUserDetails = ({
  user,
  dialogOpen,
  usersDelete,
  redirectTo,
  toastOpen
}) => (
  <DigitIfElseRendering
    test={user != null}
    ifRender={() => (
      <DigitTranslations
        translations={translations}
        uniquePath="Users.Screen.ShowUserDetails"
        render={text => (
          <DigitLayout.Center>
            <DigitDesign.Card minWidth="300px" maxWidth="600px">
              <DigitDesign.CardTitle
                text={
                  user.first_name + " '" + user.nickname + "' " + user.last_name
                }
              />
              <DigitDesign.CardBody>
                <DigitDisplayData
                  data={user}
                  keysText={{
                    cid: text.cid,
                    first_name: text.firstName,
                    last_name: text.lastName,
                    nickname: text.nick,
                    email: text.email,
                    acceptance_year: text.acceptanceYear
                  }}
                  keysOrder={[
                    "cid",
                    "first_name",
                    "last_name",
                    "nickname",
                    "email",
                    "acceptance_year"
                  ]}
                />
              </DigitDesign.CardBody>
              <DigitDesign.CardButtons reverseDirection>
                <DigitDesign.Link to={"/users/" + user.cid + "/edit"}>
                  <DigitButton text={text.Edit} primary raised />
                </DigitDesign.Link>
                <DigitLayout.Spacing />
                <DigitButton
                  onClick={() =>
                    dialogOpen({
                      title:
                        text.WouldYouLikeToDelete +
                        " " +
                        user.firstName +
                        " '" +
                        user.nick +
                        "' " +
                        user.lastName,
                      confirmButtonText: text.DeleteUser,
                      cancelButtonText: text.Cancel,
                      onConfirm: () => {
                        usersDelete(user.cid)
                          .then(response => {
                            toastOpen({
                              text:
                                text.DeleteSuccessfully +
                                " " +
                                user.firstName +
                                " '" +
                                user.nick +
                                "' " +
                                user.lastName
                            });
                            redirectTo("/users");
                          })
                          .catch(error => {
                            toastOpen({
                              text: text.SomethingWentWrong
                            });
                          });
                      }
                    })
                  }
                  text={text.Delete}
                />
              </DigitDesign.CardButtons>
            </DigitDesign.Card>
          </DigitLayout.Center>
        )}
      />
    )}
  />
);

export default ShowUserDetails;
