import {
    DigitButton,
    DigitTextField,
    DigitSelect,
    DigitLayout,
    DigitText,
    DigitTranslations,
    useDigitTranslations
} from "@cthit/react-digit-components";
import React, { useState } from "react";
import EditableWebsite from "./sub-views/editable-website";
import translations from "./EditWebsites.view.translations";

const getValueToTextMap = availableWebsites => {
    const output = {};

    availableWebsites.forEach(website => {
        output[website.name] = website.prettyName;
    });

    return output;
};

const EditWebsites = ({ push, replace, remove, form, availableWebsites }) => {
    const [
        { newWebsiteTypeSelected, newWebsiteUrl },
        setNewWebsite
    ] = useState({ newWebsiteTypeSelected: "", newWebsiteUrl: "" });
    const [text] = useDigitTranslations(translations);

    if (availableWebsites == null || availableWebsites.length === 0) {
        return (
            <>
                <DigitText.Text text={text.NoAvailableWebsites} />
                <DigitLayout.Spacing />
            </>
        );
    } else {
        return (
            <DigitLayout.Fill>
                <DigitLayout.Column>
                    <DigitLayout.Size minWidth="150px">
                        <DigitSelect
                            valueToTextMap={getValueToTextMap(
                                availableWebsites
                            )}
                            upperLabel="Typ"
                            value={newWebsiteTypeSelected}
                            onChange={e => {
                                this.setState({
                                    newWebsiteTypeSelected: e.target.value
                                });
                            }}
                        />
                    </DigitLayout.Size>

                    <DigitLayout.Spacing />

                    <DigitTextField
                        upperLabel="URL"
                        value={newWebsiteUrl}
                        onChange={e => {
                            this.setState({
                                newWebsiteUrl: e.target.value
                            });
                        }}
                    />

                    <DigitLayout.Spacing />

                    <DigitButton
                        outline
                        text="Add website"
                        onClick={() => {
                            push({
                                website: this.state.newWebsiteTypeSelected,
                                url: this.state.newWebsiteUrl
                            });
                            this.setState({
                                newWebsiteUrl: ""
                            });
                        }}
                    />
                </DigitLayout.Column>
                <DigitLayout.Fill>
                    {form.values.websites.map((website, index) => (
                        <EditableWebsite
                            key={website.url}
                            website={website.website}
                            prettyWebsite={website.website}
                            url={website.url}
                            onChange={newWebsite => {
                                replace(index, newWebsite);
                            }}
                            onDelete={() => {
                                remove(index);
                            }}
                        />
                    ))}
                </DigitLayout.Fill>
            </DigitLayout.Fill>
        );
    }
};

export default EditWebsites;
