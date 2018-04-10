/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.us.isa.sedl.core.design;

/**
 *
 * @author José Antonio Parejo
 */
public abstract class AbstractExtensionDomain extends Domain{
        @Override
        public Boolean isFinite() {
            return true;
        } 
        
        @Override
        public boolean contains(Level l)
        {
            return getLevels().contains(l);
        }
}
