package com.sun.webkit.dom;
import org.w3c.dom.html.HTMLFontElement;
public class HTMLFontElementImpl extends HTMLElementImpl implements HTMLFontElement {
HTMLFontElementImpl(long peer) {
super(peer);
}
static HTMLFontElement getImpl(long peer) {
return (HTMLFontElement)create(peer);
}
public String getColor() {
return getColorImpl(getPeer());
}
native static String getColorImpl(long peer);
public void setColor(String value) {
setColorImpl(getPeer(), value);
}
native static void setColorImpl(long peer, String value);
public String getFace() {
return getFaceImpl(getPeer());
}
native static String getFaceImpl(long peer);
public void setFace(String value) {
setFaceImpl(getPeer(), value);
}
native static void setFaceImpl(long peer, String value);
public String getSize() {
return getSizeImpl(getPeer());
}
native static String getSizeImpl(long peer);
public void setSize(String value) {
setSizeImpl(getPeer(), value);
}
native static void setSizeImpl(long peer, String value);
}