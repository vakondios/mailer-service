package com.avakio.mailer.caches;

import com.avakio.mailer.dto.AuditInfoDto;
import com.avakio.mailer.utils.CommonLib;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Creation of Audit Cache.
 * For an another cache, you change only the name
 */
@Slf4j
@Component
public class AuditCache extends Cache {

    @Autowired
    public AuditCache(CacheManager cacheManager) {
        super("audit", cacheManager);
        if (log.isDebugEnabled()) log.debug("Component Initialized.");
    }

    public AuditInfoDto getAudit(String transaction_key) {
        if (transaction_key == null) return null;
        org.springframework.cache.Cache.ValueWrapper obj = readCache(transaction_key);
        if (obj != null ) return (AuditInfoDto) obj.get();

        return null;
    }

    public String newTransaction(String trxId){
        String retStr;
        if (CommonLib.isBlankString(trxId)) {
            retStr = UUID.randomUUID().toString();
        } else {
            if (getAudit(trxId) != null) {
                retStr = UUID.randomUUID() + "(" + trxId +")";
            } else {
                retStr = trxId;
            }
        }
        return retStr;
    }

    public void newAudit(AuditInfoDto auditInfoDto) {
        String transaction_key = auditInfoDto.getTransaction_id();
        if (transaction_key != null)  newCache(transaction_key, auditInfoDto);
    }

    public void updateAudit(AuditInfoDto auditInfoDto) {
        String transaction_key = auditInfoDto.getTransaction_id();
        if (transaction_key != null)  updateCache(transaction_key, auditInfoDto);
    }

    public void deleteAudit(AuditInfoDto auditInfoDto) {
        String transaction_key = auditInfoDto.getTransaction_id();
        if (transaction_key != null) deleteCache(transaction_key);
    }
}
