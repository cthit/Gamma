import {
    DigitButton,
    DigitTranslations,
    DigitDisplayData,
    DigitDesign,
    DigitLayout,
    DigitIfElseRendering
} from "@cthit/react-digit-components";
import React from "react";
import translations from "./ShowWebsiteDetails.screen.translations.json";

const ShowWebsiteDetails = ({
    website,
    dialogOpen,
    toastOpen,
    redirectTo,
    websitesDelete
}) => (
    <DigitIfElseRendering
        test={website != null}
        ifRender={() => (
            <DigitTranslations
                translations={translations}
                uniquePath="Websites.Screen.ShowWebsiteDetails"
                render={text => (
                    <DigitLayout.Fill>
                        <DigitLayout.Center>
                            <DigitDesign.Card minWidth="300px" maxWidth="600px">
                                <DigitDesign.CardBody>
                                    <DigitDisplayData
                                        data={website}
                                        keysText={{
                                            id: text.Id,
                                            name: text.Name,
                                            prettyName: text.PrettyName
                                        }}
                                        keysOrder={["id", "name", "prettyName"]}
                                    />
                                </DigitDesign.CardBody>
                                <DigitDesign.CardButtons reverseDirection>
                                    <DigitDesign.Link
                                        to={"/websites/" + website.id + "/edit"}
                                    >
                                        <DigitButton
                                            text={text.EditWebsite}
                                            primary
                                            raised
                                        />
                                    </DigitDesign.Link>
                                    <DigitLayout.Spacing />
                                    <DigitButton
                                        onClick={() => {
                                            dialogOpen({
                                                title:
                                                    text.WouldYouLikeToDelete +
                                                    " " +
                                                    website.prettyName,
                                                confirmButtonText:
                                                    text.DeleteWebsite,
                                                cancelButtonText: text.Cancel,
                                                onConfirm: () => {
                                                    websitesDelete(website.id)
                                                        .then(response => {
                                                            toastOpen({
                                                                text:
                                                                    website.prettyName +
                                                                    " " +
                                                                    text.Deleted
                                                            });
                                                            redirectTo(
                                                                "/websites"
                                                            );
                                                        })
                                                        .catch(error => {
                                                            toastOpen({
                                                                text:
                                                                    text.SomethingWentWrong
                                                            });
                                                        });
                                                }
                                            });
                                        }}
                                        text={text.DeleteWebsite}
                                    />
                                </DigitDesign.CardButtons>
                            </DigitDesign.Card>
                        </DigitLayout.Center>
                    </DigitLayout.Fill>
                )}
            />
        )}
    />
);

export default ShowWebsiteDetails;
