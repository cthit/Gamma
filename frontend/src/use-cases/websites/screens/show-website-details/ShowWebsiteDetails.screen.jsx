import {
    DigitButton,
    DigitTranslations,
    DigitDisplayData,
    DigitDesign
} from "@cthit/react-digit-components";
import React from "react";
import { Center, Fill, Spacing } from "../../../../common-ui/layout";
import IfElseRendering from "../../../../common/declaratives/if-else-rendering";
import translations from "./ShowWebsiteDetails.screen.translations.json";

const ShowWebsiteDetails = ({
    website,
    gammaDialogOpen,
    toastOpen,
    redirectTo,
    websitesDelete
}) => (
    <IfElseRendering
        test={website != null}
        ifRender={() => (
            <DigitTranslations
                translations={translations}
                uniquePath="Websites.Screen.ShowWebsiteDetails"
                render={text => (
                    <Fill>
                        <Center>
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
                                    <Spacing />
                                    <DigitButton
                                        onClick={() => {
                                            gammaDialogOpen({
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
                        </Center>
                    </Fill>
                )}
            />
        )}
    />
);

export default ShowWebsiteDetails;
